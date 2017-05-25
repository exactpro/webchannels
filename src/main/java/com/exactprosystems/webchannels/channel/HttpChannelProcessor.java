package com.exactprosystems.webchannels.channel;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.exactprosystems.webchannels.messages.PollingRequest;

public class HttpChannelProcessor extends AbstractChannelProcessor{
	
	public HttpChannelProcessor(AbstractHandlerFactory handlerFactory,
			AbstactMessageFactory messageFactory,
			ChannelSettings settings) {
		
		super(handlerFactory, messageFactory, settings,
				new HttpChannelFactory(messageFactory, handlerFactory));
		
	}
	
	public void processAsyncContext(AsyncContext context) {
		
		String channelId = context.getRequest().getParameter("channelId");
		
		if (channelId == null) {
			throw new RuntimeException("ChannelId is not defined");
		}
		
		AbstractChannel channel = channels.get(channelId);

		if (channel == null) {
			HttpSession session = ((HttpServletRequest) context.getRequest()).getSession(true);
			ChannelSettings settings = getSettings(session, context.getRequest());
			channel = channelFactory.createChannel(channelId, settings, executor);
			AbstractChannel prev = channels.putIfAbsent(channelId, channel);
			if (prev != null) {
				channel.close();
				channel = prev;
			} else {
				channel.initHandler();
				SessionContrtoller.getInstance().registerChannel(channel, session);
			}
		}
		
		List<WithSeqnumWrapper> list = null;
		
		try {
			if (channel.getChannelSettings().isCompressionEnabled()) {
				try (InputStream input = context.getRequest().getInputStream();
						InputStream gzipInput = new InflaterInputStream(input)) {
					list = messageFactory.decodeMessage(gzipInput);
				}
			} else {
				try (Reader input = context.getRequest().getReader()) {
					list = messageFactory.decodeMessage(input);
				}
			}
		} catch (Exception e) {
			logger.error("Exception while decoding input messages", e);
			Throwable[] suppressed = e.getSuppressed();
			for (Throwable throwable : suppressed) {
				logger.error("Suppressed exception during decoding in channel " + channel, throwable);
			}
			context.complete();
		}
		
		
		logger.trace("Process AsyncContext {} for {}", context, channel);
		
		if (list != null) {
			if (list.size() == 1 && list.get(0).getMessage() instanceof PollingRequest) {
				channel.bind(context);
			} else {
				for (WithSeqnumWrapper wrapper : list) {
					logger.trace("Processor {} onMessage() {} for {}", this, wrapper, channel);
					channel.handleRequest(wrapper, null);
				}
				context.complete();
			}
		
		}
		
	}
	
	public void processAsyncContextClose(AsyncEvent event) {
		
		try {
			
			String channelId = event.getSuppliedRequest().getParameter("channelId");
			
			if (channelId == null) {
				throw new RuntimeException("ChannelId is not defined");
			}
			
			AbstractChannel channel = channels.get(channelId);
			
			if (channel != null) {
				logger.trace("Close AsyncContext {} for {}", event.getAsyncContext(), channel);
				channel.unbind(event.getAsyncContext());
			}
			
		} catch (Exception e) {
			
			logger.error("Exception while processing asyncContext", e);
			
		}
		
	}
	
	private ChannelSettings getSettings(HttpSession session, ServletRequest request) {
		Boolean compressionEnabled = getSaveValue((Boolean) session.getAttribute("compressionEnabled"), settings.isCompressionEnabled());
		Boolean compressionSupported = getSaveValue(Boolean.valueOf(request.getParameter("compressionSupported")), Boolean.TRUE);
		return new ChannelSettings(
					getSaveValue((Long) session.getAttribute("pollingInterval"), settings.getPollingInterval()), 
					getSaveValue((Long) session.getAttribute("heartbeatInterval"), settings.getHeartBeatInterval()), 
					settings.getMaxCountToSend(), 
					settings.getExecutorBatchSize(), 
					getSaveValue((Long) session.getAttribute("sessionDestroyInterval"), settings.getDisconnectTimeout()),
					settings.getThreadCount(), 
					settings.getResendBufferSize(),
					compressionEnabled && compressionSupported);
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
		return "HttpChannelProcessor[]";
	}

}
