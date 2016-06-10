package com.exactprosystems.ticker.channel;

import com.exactprosystems.ticker.messages.BusinessMessage;
import com.exactprosystems.ticker.messages.ChannelsMessage;

@ChannelsMessage
public class TestBusinessMessage extends BusinessMessage {

	@Override
	public String toString() {
		return "TestBusinessMessage []";
	}

}
