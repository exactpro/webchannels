package com.exactprosystems.webchannels.channel;

import java.util.concurrent.ExecutorService;

import javax.servlet.http.HttpSession;

public class WebSocketChannelFactory extends AbstractChannelFactory {
	
	public WebSocketChannelFactory(AbstactMessageFactory messageFactory, AbstractHandlerFactory handlerFactory) {
		super(messageFactory, handlerFactory);
	}
	
	@Override
	public AbstractChannel createChannel(String channelId, ChannelSettings settings, ExecutorService executor, HttpSession httpSession) {
		return new WebSocketChannel(getHandlerFactory().createHandler(), channelId, settings, getMessageFactory(), executor, httpSession);
	}

}
