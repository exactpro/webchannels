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

package com.exactprosystems.webchannels.web;

import java.io.Reader;
import java.util.List;

import javax.websocket.MessageHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.webchannels.channel.AbstactMessageFactory;
import com.exactprosystems.webchannels.channel.AbstractChannel;
import com.exactprosystems.webchannels.channel.WithSeqnumWrapper;

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
				channel.handleRequest(request);
			}
		}
		
	}

	@Override
	public String toString() {
		return "SocketMessageHandler[]";
	}

}
