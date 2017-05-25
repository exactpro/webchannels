package com.exactprosystems.webchannels.channel;

import java.util.ArrayList;
import java.util.List;

public class InputMessagesBuffer {
	
	private WithSeqnumWrapper[] messages;
	
	private long from;
	
	private long to;
	
	private boolean recovered;
	
	public InputMessagesBuffer() {
		messages = null;
		from = 0;
		to = 0;
		recovered = true;
	}
	
	public boolean isRecovered() {
		return recovered;
	}

	public void add(WithSeqnumWrapper wrapper) {
		if (recovered) {
			throw new RuntimeException("Buffer not in recover state");
		} else {
			int position = (int) (wrapper.getSeqnum() - from);
			if (position >= 0) {
				if (position >= messages.length) {
					WithSeqnumWrapper[] tmp = new WithSeqnumWrapper[position + 1];
					System.arraycopy(messages, 0, tmp, 0, messages.length);
					messages = tmp;
				}
				messages[position] = wrapper;
			}
		}
	}

	public void recover(long expectedSeqnum, long seqnum) {
		if (seqnum <= expectedSeqnum) {
			throw new RuntimeException();
		}
		if (recovered) {
			recovered = false;
			from = expectedSeqnum;
			to = seqnum;
			messages = new WithSeqnumWrapper[(int) (to - from)];
		} else {
			if (expectedSeqnum < from) {
				from = expectedSeqnum;
			}
			if (seqnum > to) {
				to = seqnum;
			}
			WithSeqnumWrapper[] tmp = new WithSeqnumWrapper[(int) (to - from)];
			System.arraycopy(messages, 0, tmp, 0, messages.length);
			messages = tmp;
		}
	}

	public List<WithSeqnumWrapper> tryRecover() {
		List<WithSeqnumWrapper> result = new ArrayList<WithSeqnumWrapper>();
		int i = 0;
		for (; i < messages.length; i++) {
			WithSeqnumWrapper message = messages[i];
			if (message != null) {
				result.add(message);
			} else {
				break;
			}
		}
		WithSeqnumWrapper[] tmp = new WithSeqnumWrapper[messages.length - i];
		System.arraycopy(messages, i, tmp, 0, messages.length - i);
		messages = tmp;
		from = from + i;
		if (messages.length == 0) {
			recovered = true;
		}
		return result;
	}

	public void clear() {
		messages = null;
		from = 0;
		to = 0;
		recovered = true;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}

}
