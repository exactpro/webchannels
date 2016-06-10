package com.exactprosystems.webchannels.channel;

public class CreateChannelEvent {

	private static final CreateChannelEvent instance = new CreateChannelEvent();
	
	private CreateChannelEvent() {
		// TODO Auto-generated constructor stub
	}
	
	public static CreateChannelEvent getInstance() {
		return instance;
	}
	
	@Override
	public String toString() {
		return "CreateChannelMessage[]";
	}
	
}
