package com.exactprosystems.ticker.web;

import java.io.Reader;
import java.util.List;

import javax.websocket.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.ticker.channel.AbstactMessageFactory;
import com.exactprosystems.ticker.channel.AbstractChannel;
import com.exactprosystems.ticker.channel.WithSeqnumWrapper;

public class TextMessageHandler implements MessageHandler.Whole<Reader> {

	private static final Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);
	
	private final AbstactMessageFactory messageFactory;
	
	private final AbstractChannel channel;
	
	public TextMessageHandler(AbstactMessageFactory messageFactory, AbstractChannel channel) {
		
		logger.info("Create {} for {}", this, channel);
		
		this.messageFactory = messageFactory;
		this.channel = channel;
		
	}

	@Override
	public void onMessage(Reader input) {
		
		List<WithSeqnumWrapper> list = null;
		
		try {
			list = messageFactory.decodeMessage(input);
		} catch (Exception e) {
			logger.error("Exception while decoding input messages", e);
			Throwable[] suppressed = e.getSuppressed();
			for (Throwable throwable : suppressed) {
				logger.error("Suppressed exception during decoding in channel " + channel, throwable);
			}
		}
		
		if (list != null) {
			for (WithSeqnumWrapper request : list) {
				logger.trace("MessageHandler {} onMessage() {} for {}", this, request, channel);
				channel.handleRequest(request, null);
			}
		}
		
	}

	@Override
	public String toString() {
		return "SocketMessageHandler[]";
	}

}
