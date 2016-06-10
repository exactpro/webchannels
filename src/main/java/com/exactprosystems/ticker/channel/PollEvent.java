package com.exactprosystems.ticker.channel;

public class PollEvent {

	private final static PollEvent instance = new PollEvent();
	
	private PollEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public static PollEvent getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TickMessage[]");
		return builder.toString();
	}

}
