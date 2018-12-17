/*
 * *****************************************************************************
 *  Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

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
