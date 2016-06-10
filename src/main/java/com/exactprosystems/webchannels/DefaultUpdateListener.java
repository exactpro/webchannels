package com.exactprosystems.webchannels;

import com.exactprosystems.webchannels.channel.AbstractChannel;

public abstract class DefaultUpdateListener implements IUpdateRequestListener {

	private final String id;
	
	private final AbstractChannel channel;
	
	private final IUpdateFilter filter;
	
	public DefaultUpdateListener(String id, AbstractChannel channel, IUpdateFilter filter) {
		super();
		this.id = id;
		this.channel = channel;
		this.filter = filter;
	}

	@Override
	public IUpdateFilter getFilter() {
		return filter;
	}

	@Override
	public String getId() {
		return id;
	}
	
	protected AbstractChannel getChannel() {
		return channel;
	}

}
