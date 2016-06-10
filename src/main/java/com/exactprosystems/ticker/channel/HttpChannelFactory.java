package com.exactprosystems.ticker.channel;

import java.util.concurrent.ExecutorService;

public class HttpChannelFactory extends AbstractChannelFactory {

	public HttpChannelFactory(AbstactMessageFactory messageFactory, AbstractHandlerFactory handlerFactory) {
		super(messageFactory, handlerFactory);
	}
	
	@Override
	public AbstractChannel createChannel(String channelId, ChannelSettings settings, ExecutorService executor) {
		return new HttpChannel(getHandlerFactory().createHandler(), channelId, settings, getMessageFactory(), executor);
	}

}
