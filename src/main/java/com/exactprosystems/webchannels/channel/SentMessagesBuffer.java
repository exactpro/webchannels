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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.exactprosystems.webchannels.exceptions.RecoverException;

public class SentMessagesBuffer {

	private final CircularFifoBuffer messageQueue;
	
	private final int capacity;
	
	private long lastSeqnum;
	
	public SentMessagesBuffer(int capacity) {
		this.lastSeqnum = 0;
		this.capacity = capacity;
		this.messageQueue = new CircularFifoBuffer(capacity);
	}
	
	public void add(WithSeqnumWrapper message) {
		lastSeqnum = message.getSeqnum();
		messageQueue.add(message);
	}

	public List<WithSeqnumWrapper> get(long from, long to) throws RecoverException {
		long firstSeqnum = lastSeqnum - capacity;
		if (from < firstSeqnum || to > lastSeqnum) {
			throw new RecoverException("Failed to resend messages from " + from + " to " + to);
		}
		List<WithSeqnumWrapper> list = new ArrayList<WithSeqnumWrapper>();
		for (Object object : messageQueue) {
			WithSeqnumWrapper wrapper = (WithSeqnumWrapper) object;
			if (wrapper.getSeqnum() >= from && wrapper.getSeqnum() < to) {
				list.add(wrapper);
			}
		}
		return list;
	}

	public void clear() {
		messageQueue.clear();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageBuffer[messageQueueSize=");
		builder.append(messageQueue.size());
		builder.append("]");
		return builder.toString();
	}

}
