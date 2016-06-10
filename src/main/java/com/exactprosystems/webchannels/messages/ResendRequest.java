package com.exactprosystems.webchannels.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

@ChannelsMessage
public class ResendRequest extends AdminMessage {

	private final String requestId;
	
	private final long from;
	
	private final long to;

	public ResendRequest(@JsonProperty("requestId") String requestId, 
			@JsonProperty("from") long from, 
			@JsonProperty("to") long to) {
		super();
		this.requestId = requestId;
		this.from = from;
		this.to = to;
	}

	public String getRequestId() {
		return requestId;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResendRequest[requestId=");
		builder.append(requestId);
		builder.append(",from=");
		builder.append(from);
		builder.append(",to=");
		builder.append(to);
		builder.append("]");
		return builder.toString();
	}
	
}
