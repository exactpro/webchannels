package com.exactprosystems.webchannels.web;

import java.io.InputStream;
import java.util.List;
import java.util.zip.InflaterInputStream;

import javax.websocket.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.webchannels.channel.AbstactMessageFactory;
import com.exactprosystems.webchannels.channel.AbstractChannel;
import com.exactprosystems.webchannels.channel.WithSeqnumWrapper;

public class BinaryMessageHandler implements MessageHandler.Whole<InputStream> {

	private static final Logger logger = LoggerFactory.getLogger(BinaryMessageHandler.class);
	
	private final AbstactMessageFactory messageFactory;
	
	private final AbstractChannel channel;
	
	public BinaryMessageHandler(AbstactMessageFactory messageFactory, AbstractChannel channel) {
		
		logger.info("Creating MessageHandler {} for channel {}", this, channel);
		
		this.messageFactory = messageFactory;
		this.channel = channel;
		
	}

	@Override
	public void onMessage(InputStream input) {
		
		List<WithSeqnumWrapper> list = null;
		
		try (InputStream gzipInput = new InflaterInputStream(input)) {
			list = messageFactory.decodeMessage(gzipInput);
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
