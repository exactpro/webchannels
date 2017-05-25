package com.exactprosystems.webchannels;

import com.exactprosystems.webchannels.channel.AbstractChannel;

public abstract class DefaultUpdateListener implements IUpdateRequestListener {

	private final String id;
	
	private final AbstractChannel channel;
	
	public DefaultUpdateListener(String id, AbstractChannel channel) {
		super();
		this.id = id;
		this.channel = channel;
	}

	@Override
	public String getId() {
		return id;
	}
	
	protected AbstractChannel getChannel() {
		return channel;
	}

}
