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

import com.exactprosystems.webchannels.messages.AbstractMessage;
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
