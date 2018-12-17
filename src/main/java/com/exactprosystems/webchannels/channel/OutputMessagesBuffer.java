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
import java.util.LinkedList;
import java.util.List;

public class OutputMessagesBuffer {

	private final LinkedList<WithSeqnumWrapper> outputMessageQueue;

	public OutputMessagesBuffer() {
		outputMessageQueue = new LinkedList<WithSeqnumWrapper>();
	}
	
	public void offer(WithSeqnumWrapper wrapper) {
		outputMessageQueue.offer(wrapper);
	}

	public void offerFirst(List<WithSeqnumWrapper> messages) {
		for (int i = messages.size() - 1; i >= 0; i--) {
			outputMessageQueue.offerFirst(messages.get(i));
		}
	}
	
	public boolean isEmpty() {
		return outputMessageQueue.isEmpty();
	}

	public List<WithSeqnumWrapper> poll(int size) {
		List<WithSeqnumWrapper> messages = new ArrayList<WithSeqnumWrapper>();
		int count = 0;
		while (!outputMessageQueue.isEmpty() && count < size) {
			WithSeqnumWrapper wrapper = outputMessageQueue.poll();
			messages.add(wrapper);
			++count;
		}
		return messages;
	}

	public void clear() {
		outputMessageQueue.clear();
	}
	
}
