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
