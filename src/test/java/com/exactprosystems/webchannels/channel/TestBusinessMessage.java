package com.exactprosystems.webchannels.channel;

import com.exactprosystems.webchannels.messages.BusinessMessage;
import com.exactprosystems.webchannels.messages.ChannelsMessage;

@ChannelsMessage
public class TestBusinessMessage extends BusinessMessage {

	@Override
	public String toString() {
		return "TestBusinessMessage []";
	}

}
