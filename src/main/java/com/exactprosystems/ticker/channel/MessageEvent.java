package com.exactprosystems.ticker.channel;

public class MessageEvent {

	private final Object message;
	
	private final Direction direction;

	public MessageEvent(Object message, Direction direction, Object context) {
		super();
		this.message = message;
		this.direction = direction;
	}

	public Object getMessage() {
		return message;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageWrapper[message=");
		builder.append(message);
		builder.append(",direction=");
		builder.append(direction);
		builder.append("]");
		return builder.toString();
	}
	
}
