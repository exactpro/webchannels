package com.exactprosystems.ticker.channel;

import com.exactprosystems.ticker.messages.AbstractMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WithSeqnumWrapper {

	private final long seqnum;
	
	private final AbstractMessage message;

	public WithSeqnumWrapper(@JsonProperty("seqnum") long seqnum, 
			@JsonProperty("message") AbstractMessage message) {
		super();
		this.seqnum = seqnum;
		this.message = message;
	}
	
	public long getSeqnum() {
		return seqnum;
	}

	public AbstractMessage getMessage() {
		return message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SeqnumWrapper[seqnum=");
		builder.append(seqnum);
		builder.append(",message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
	
}
