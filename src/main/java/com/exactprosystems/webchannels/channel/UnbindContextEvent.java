package com.exactprosystems.webchannels.channel;

public class UnbindContextEvent {

	private final Object context;
	
	public UnbindContextEvent(Object context) {
		super();
		this.context = context;
	}

	public Object getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnbindContextMessage[context=");
		builder.append(context);
		builder.append("]");
		return builder.toString();
	}

}
