package com.exactprosystems.webchannels.channel;

import java.util.concurrent.ExecutorService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

public class HttpChannelFactory extends AbstractChannelFactory {

	public HttpChannelFactory(AbstactMessageFactory messageFactory, AbstractHandlerFactory handlerFactory) {
		super(messageFactory, handlerFactory);
	}
	
	@Override
	public AbstractChannel createChannel(String channelId, ChannelSettings settings, ExecutorService executor) {
		return new HttpChannel(getHandlerFactory().createHandler(), channelId, settings, getMessageFactory(), executor);
	}

}
