package com.exactprosystems.ticker.channel;

public class CloseChannelEvent {

	private static final CloseChannelEvent instance = new CloseChannelEvent();
	
	private CloseChannelEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public static CloseChannelEvent getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		return "CloseChannelMessage[]";
	}

}
