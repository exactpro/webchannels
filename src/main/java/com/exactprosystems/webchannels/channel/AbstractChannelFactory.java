package com.exactprosystems.webchannels.channel;

import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpSession;

/**
 * Abstract class for creating concrete instances of {@link AbstractChannel}
 * 
 * @author dmitry.zavodchikov
 *
 */
public abstract class AbstractChannelFactory {
	
	private final AbstactMessageFactory messageFactory;
	
	private final AbstractHandlerFactory handlerFactory;
	
	public AbstractChannelFactory(AbstactMessageFactory messageFactory,
			AbstractHandlerFactory handlerFactory) {
		this.messageFactory = messageFactory;
		this.handlerFactory = handlerFactory;
	}
	
	public AbstactMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public AbstractHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public abstract AbstractChannel createChannel(String channelId, ChannelSettings settings, ExecutorService executor, HttpSession httpSession);
	
}
