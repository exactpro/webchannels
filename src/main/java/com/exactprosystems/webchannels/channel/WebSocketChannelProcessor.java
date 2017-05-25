package com.exactprosystems.webchannels.channel;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

import com.exactprosystems.webchannels.web.BinaryMessageHandler;
import com.exactprosystems.webchannels.web.TextMessageHandler;

public class WebSocketChannelProcessor extends AbstractChannelProcessor{
	
	public WebSocketChannelProcessor(AbstractHandlerFactory handlerFactory, AbstactMessageFactory messageFactory, ChannelSettings settings) {
		
		super(handlerFactory, messageFactory, settings, 
				new WebSocketChannelFactory(messageFactory, handlerFactory));
		
	}
	
	public void processWSRequest(Session session, EndpointConfig config) {
		
		String channelId = session.getRequestParameterMap().get("channelId").get(0);
		
		if (channelId == null) {
			throw new RuntimeException("Requested channelId is not defined");
		}
		
		AbstractChannel channel = channels.get(channelId);
		
		if (channel == null) {
			HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
			ChannelSettings settings = getSettings(httpSession, null);
			channel = channelFactory.createChannel(channelId, settings, executor);
			AbstractChannel prev = channels.putIfAbsent(channel.getID(), channel);
			if (prev != null) {
				channel.close();
				channel = prev;
			} else {
				channel.initHandler();
				SessionContrtoller.getInstance().registerChannel(channel, httpSession);
			}
		}
		
		logger.trace("Process WebSocket {} for {}", session, channel);
		
		if (channel.getChannelSettings().isCompressionEnabled()) {
			BinaryMessageHandler messageHandler = new BinaryMessageHandler(messageFactory, channel);
			session.addMessageHandler(messageHandler);
			channel.bind(session);
		} else {
			TextMessageHandler messageHandler = new TextMessageHandler(messageFactory, channel);
			session.addMessageHandler(messageHandler);
			channel.bind(session);
		}
		
	}
	
	public void processWSClose(Session session, CloseReason reason) {
		
		String channelId = session.getRequestParameterMap().get("channelId").get(0);
		
		if (channelId == null) {
			throw new RuntimeException("Requested channelId is not defined");
		}
		
		AbstractChannel channel = channels.get(channelId);
		
		if (channel != null) {
			logger.trace("Close WebSocket {} for {} with reason {}", session, channel, reason);
			channel.unbind(session);
		}
		
	}
	
	private ChannelSettings getSettings(HttpSession session, HandshakeRequest request) {
		return new ChannelSettings(
					getSaveValue((Long) session.getAttribute("pollingInterval"), settings.getPollingInterval()), 
					getSaveValue((Long) session.getAttribute("heartbeatInterval"), settings.getHeartBeatInterval()), 
					settings.getMaxCountToSend(), 
					settings.getExecutorBatchSize(), 
					getSaveValue((Long) session.getAttribute("sessionDestroyInterval"), settings.getDisconnectTimeout()),
					settings.getThreadCount(),
					settings.getResendBufferSize(),
					getSaveValue((Boolean) session.getAttribute("compressionEnabled"), settings.isCompressionEnabled()));
	}

	private <T> T getSaveValue(T value, T defaultValue) {
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	@Override
	public String toString() {
		return "WebSocketChannelProcessor[]";
	}
	
}
