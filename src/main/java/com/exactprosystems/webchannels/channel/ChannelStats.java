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

package com.exactprosystems.webchannels.channel;

import com.exactprosystems.webchannels.enums.ChannelStatus;

public class ChannelStats {

	private final String channelId;
	
	private final ChannelStatus status;
	
	private final long created;
	
	private final long closed;
	
	private final long sent;
	
	private final long received;
	
	private final long lastSend;
	
	private final long lastReceive;

	public ChannelStats(String channelId, ChannelStatus status, long created,
			long closed, long sent, long received, long lastSend, long lastReceive) {
		super();
		this.channelId = channelId;
		this.status = status;
		this.created = created;
		this.closed = closed;
		this.sent = sent;
		this.received = received;
		this.lastSend = lastSend;
		this.lastReceive = lastReceive;
	}

	public long getSent() {
		return sent;
	}

	public long getReceived() {
		return received;
	}

	public long getLastSend() {
		return lastSend;
	}

	public long getLastReceive() {
		return lastReceive;
	}

	public String getChannelId() {
		return channelId;
	}

	public ChannelStatus getStatus() {
		return status;
	}

	public long getCreated() {
		return created;
	}

	public long getClosed() {
		return closed;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelStats [channelId=");
		builder.append(channelId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", created=");
		builder.append(created);
		builder.append(", closed=");
		builder.append(closed);
		builder.append(", sent=");
		builder.append(sent);
		builder.append(", received=");
		builder.append(received);
		builder.append(", lastSend=");
		builder.append(lastSend);
		builder.append(", lastReceive=");
		builder.append(lastReceive);
		builder.append("]");
		return builder.toString();
	}
	
}
