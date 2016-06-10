package com.exactprosystems.ticker.channel;

public class BindContextEvent {

	private final Object context;
	
	public BindContextEvent(Object context) {
		super();
		this.context = context;
	}

	public Object getContext() {
		return context;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BindContextMessage[context=");
		builder.append(context);
		builder.append("]");
		return builder.toString();
	}

}
