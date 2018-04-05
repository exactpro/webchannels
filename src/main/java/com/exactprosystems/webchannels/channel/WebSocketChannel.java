package com.exactprosystems.webchannels.channel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import com.exactprosystems.webchannels.enums.ChannelStatus;
import com.exactprosystems.webchannels.exceptions.RecoverException;
import com.exactprosystems.webchannels.messages.AdminMessage;
import com.exactprosystems.webchannels.messages.CloseChannel;
import com.exactprosystems.webchannels.messages.HeartBeat;
import com.exactprosystems.webchannels.messages.ResendRequest;
import com.exactprosystems.webchannels.messages.TestRequest;

public class WebSocketChannel extends AbstractChannel {
	
	private OutputMessagesBuffer outputMessageQueue;
	
	private InputMessagesBuffer inputMessageQueue;
	
	private SentMessagesBuffer sentMessageQueue;
	
	private long created;
	
	private long closed;
	
	private long lastSendTime;
	
	private long lastReceiveTime;
	
	private boolean awaitHeartbeat;
	
	private Session socketContext;
	
	private long inputSeqnum;
	
	private long outputSeqnum;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

	private long lastResendRequestTime;
	
	public WebSocketChannel(IChannelHandler handler, String channelId, ChannelSettings settings,
			AbstactMessageFactory messageFactory, ExecutorService executor, HttpSession httpSession) {
		
		super(handler, channelId, settings, messageFactory, executor, httpSession);
		
		outputMessageQueue = new OutputMessagesBuffer();
		inputMessageQueue = new InputMessagesBuffer();
		sentMessageQueue = new SentMessagesBuffer(settings.getResendBufferSize());
		lastSendTime = System.currentTimeMillis();
		lastReceiveTime = lastSendTime;
		awaitHeartbeat = false;
		inputSeqnum = 0;
		outputSeqnum = 1;
		socketContext = null;
		logger.info("Create {}", this);
		
	}
	
	@Override
	protected void onCreate() {
		this.created = System.currentTimeMillis();
		this.getHandler().onCreate(this);
	}
	
	@Override
	protected ChannelStats getChannelStats() {
		return new ChannelStats(getID(), getStatus(), created, closed, outputSeqnum, inputSeqnum, lastSendTime, lastReceiveTime);
	}
	
	@Override
	protected void onBind(Object context) {
		
		if (context == null) {
			throw new RuntimeException("Session is null");
		}
		
		Session session = (Session) context;
		
		if (socketContext != null) {
			try {
				completeRequest(socketContext);
			} catch (Exception e) {
				logger.error("Exception while closing context " + socketContext, e);
				this.getHandler().onException(e);
			} finally {
				logger.trace("Unbind context {} for {}", socketContext, this);
				socketContext = null;
			}
		}
		
		socketContext = session;
		this.setStatus(ChannelStatus.OPENED);
		logger.trace("Bind context {} for {}", session, this);
		
	}
	
	@Override
	protected void onUnbind(Object context) {
		
		if (context == null) {
			throw new RuntimeException("Session is null");
		}
		
		Session session = (Session) context;
		
		if (socketContext != null && socketContext == session) {
			try {
				completeRequest(socketContext);
			} catch (Exception e) {
				logger.error("Exception while closing context " + socketContext, e);
				this.getHandler().onException(e);
			} finally {
				logger.trace("Unbind context {} for {}", socketContext, this);
				this.setStatus(ChannelStatus.WAITING);
				socketContext = null;
			}
		}
		
	}

	@Override
	protected void processInputMessage(WithSeqnumWrapper wrapper) {
		
		Object message = wrapper.getMessage();
		long seqnum = wrapper.getSeqnum();
		long expectedSeqnum = inputSeqnum + 1;
		
		if (message instanceof AdminMessage) {
			
			handleAdminMessage((AdminMessage) message, expectedSeqnum);
			
		}
		
		if (seqnum == expectedSeqnum) {
		
			if (inputMessageQueue.isRecovered()) {
				//if (message instanceof BusinessMessage) {
					handleBusinessMessage(message, seqnum);
				//}
			} else {
				logger.info("Stash message with seqnum {} on {}", seqnum, this);
				inputMessageQueue.add(wrapper);
			}
			
		} else	if (seqnum > expectedSeqnum) {
				
			logger.error("Missed messages from {} to {} on {}", expectedSeqnum, seqnum, this);
			this.sendMessage(new ResendRequest("Resend", expectedSeqnum, seqnum));
			this.lastResendRequestTime = System.currentTimeMillis();
			
			logger.info("Init recover on {}", this);
			inputMessageQueue.recover(expectedSeqnum, seqnum);
			
			logger.info("Stash message with seqnum {} on {}", seqnum, this);
			inputMessageQueue.add(wrapper);
		
		} else if (seqnum < expectedSeqnum) {
			
			if (inputMessageQueue.isRecovered()) {
				
				logger.error("Unexpected message with seqnum {} (expected seqnum {}) on {}", seqnum, expectedSeqnum, this);
				
			} else {
				
				logger.info("Stash message with seqnum {} on {}", seqnum, this);
				inputMessageQueue.add(wrapper);
				
				List<WithSeqnumWrapper> messages = inputMessageQueue.tryRecover();
				for (WithSeqnumWrapper restored : messages) {
					//if (restored.getMessage() instanceof BusinessMessage) {
						handleBusinessMessage(restored.getMessage(), restored.getSeqnum());
					//}
				}
				
				if (inputMessageQueue.isRecovered()) {
					logger.info("Recovere complete on {}", this);
				}
				
			}
			
		}
		
		if (seqnum > inputSeqnum) {
			inputSeqnum = seqnum;
		}
		
		if (inputMessageQueue.isRecovered()) {
			lastReceiveTime = System.currentTimeMillis();
		} else {
			if (System.currentTimeMillis() - this.lastResendRequestTime > this.getChannelSettings().getHeartBeatInterval()) {
				logger.error("Try to recover messages from {} to {} again on {}", inputMessageQueue.getFrom(), inputMessageQueue.getTo(), this);
				this.sendMessage(new ResendRequest("Resend", inputMessageQueue.getFrom(), inputMessageQueue.getTo()));
				this.lastResendRequestTime = System.currentTimeMillis();
			}
		}
		
	}
	
	private void handleBusinessMessage(Object message, long seqnum) {
		Object response = this.getHandler().onReceive(message, seqnum);
		if (response != null) {
			this.sendMessage(response);
		}
	}
	
	private void handleAdminMessage(AdminMessage message, long seqnum) {
		// Handle admin message
    	if (message instanceof TestRequest) {
			logger.warn("TestRequest received on {}", this);
			this.sendMessage(new HeartBeat());
		} else if (message instanceof HeartBeat) {
			logger.debug("HeartBeat received on {}", this);
			if (awaitHeartbeat) {
				awaitHeartbeat = false;
			}
		} else if (message instanceof ResendRequest) {
			logger.error("ResendRequest {} received on {}", message, this);
			ResendRequest resendRequest = (ResendRequest) message;
            try {
                List<WithSeqnumWrapper> messages = sentMessageQueue.get(resendRequest.getFrom(), resendRequest.getTo());
                for (WithSeqnumWrapper old : messages) {
                	logger.info("Resend message {} on {}", old, this);
                    outputMessageQueue.offer(old);
                }
            } catch (RecoverException e) {
            	logger.error("Failed to handle ResendRequest", e);
            	onClose();
            }
        } else if (message instanceof CloseChannel) {
        	logger.debug("CloseChannel received on {}", this);
        	onClose();
        } else {
        	throw new RuntimeException("Unsupported message " + message);
        }
	}
	
	@Override
	protected void processOutputMessage(Object message) {
		
		WithSeqnumWrapper wrapper = new WithSeqnumWrapper(outputSeqnum++, message);
		outputMessageQueue.offer(wrapper);
		sentMessageQueue.add(wrapper);
		this.getHandler().onSend(wrapper.getMessage(), wrapper.getSeqnum());

		onPoll();
		
	}
	
	@Override
	protected void onPoll() {
		
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastReceiveTime > this.getChannelSettings().getDisconnectTimeout()) {
			logger.debug("No activity from client on {}", this);
			onClose();
			return;
		}
		
		if ((currentTime - lastReceiveTime > this.getChannelSettings().getHeartBeatInterval() * 2) && !awaitHeartbeat) {
			logger.warn("Sending testRequest for {}", this);
			this.sendMessage(new TestRequest());
			awaitHeartbeat = true;
		}
		
		if (socketContext != null) {
			
			if (!outputMessageQueue.isEmpty()) {
					
				if (socketContext.isOpen()) {
				
					List<WithSeqnumWrapper> messages = null;
					
					long sendStart = System.currentTimeMillis();
					
					try {
					
						logger.trace("Start sending messages for {}", this);
						
						messages = outputMessageQueue.poll(this.getChannelSettings().getMaxCountToSend());
						
						if (getChannelSettings().isCompressionEnabled()) {
							try (OutputStream output = socketContext.getBasicRemote().getSendStream();
									OutputStream gzipOutput = new DeflaterOutputStream(output)) {
								this.getMessageFactory().encodeMessage(messages, gzipOutput);
							}
						} else {
							try (Writer output = socketContext.getBasicRemote().getSendWriter()) {
								this.getMessageFactory().encodeMessage(messages, output);
							}
						}
						
						lastSendTime = currentTime;
						
						logger.debug("Send {} messages through {}", messages.size(), this);
						
						long sendDuration = System.currentTimeMillis() - sendStart;
						
						if (sendDuration > 200L) {
							logger.warn("Send messages via {} took {} ms", this, sendDuration);
						} else {
							logger.trace("Send messages via {} took {} ms", this, sendDuration);
						}
					
					} catch (Exception e)	{
						
						if (messages != null) {
							outputMessageQueue.offerFirst(messages);
						}
						
						logger.error("Exception while processing queue for " + this, e);
						this.getHandler().onException(e);
						
						Throwable[] suppressed = e.getSuppressed();
						for (Throwable throwable : suppressed) {
							logger.error("Suppressed exception during encoding in " + this, throwable);
							this.getHandler().onException(throwable);
						}
						
						try {
							completeRequest(socketContext);
						} catch (Exception e1) {
							logger.error("Exception while closing context " + socketContext, e1);
							this.getHandler().onException(e1);
						} finally {
							logger.trace("Unbind context {} for {}", socketContext, this);
							this.setStatus(ChannelStatus.WAITING);
							socketContext = null;
						}
						
					}
				
				} else {
					
					//Invalidate context
					try {
						completeRequest(socketContext);	
					} catch (Exception e) {
						logger.error("Exception while closing context " + socketContext, e);
						this.getHandler().onException(e);
					} finally {
						logger.trace("Unbind context {} for {}", socketContext, this);
						socketContext = null;
					}
					
				}
			
			} else {
				
				if (logger.isTraceEnabled()) {
					logger.trace("Last send: {} for {}", dateFormat.format(new Date(lastSendTime)), this);
				}
				
				if (currentTime - lastSendTime > this.getChannelSettings().getHeartBeatInterval()) {
					logger.debug("Invoke onIdle for {}", this);
					this.sendMessage(new HeartBeat());
					this.getHandler().onIdle();					
				} else {
					logger.trace("Wait HeartBeatInterval for {}", this);
				}
				
			}
		
		} else {
				
			if (logger.isTraceEnabled()) {
				logger.trace("Last send: {} for {} ", dateFormat.format(new Date(lastSendTime)), this);
			}
			
			if (currentTime - lastSendTime > this.getChannelSettings().getDisconnectTimeout()) {
				logger.debug("Server not send data too long on {}", this);
				onClose();
			} else {
				logger.trace("Wait DisconnectTimeout - {}", this);
			}
			
		}
		
	}

	@Override
	protected void onClose() {
		
		logger.info("Close {}", this);
		
		this.closed = System.currentTimeMillis();
		this.outputMessageQueue.clear();
		this.sentMessageQueue.clear();
		this.inputMessageQueue.clear();
		this.setStatus(ChannelStatus.CLOSED);
		this.getHandler().onClose();
	
		if (socketContext != null) {
			try {
				completeRequest(socketContext);
			} catch (Exception e) {
				logger.error("Exception while closing context " + socketContext, e);
				this.getHandler().onException(e);
			} finally {
				logger.trace("Unbind context {} for {}", socketContext, this);
				socketContext = null;
			}
		}
		
	}
	
	private void completeRequest(Session socket) throws IOException {
		socket.close();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WebSocketChannel[channelId=");
		builder.append(getID());
		builder.append(",status=");
		builder.append(getStatus());
		builder.append("]");
		return builder.toString();
	}
	
}
