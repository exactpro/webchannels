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

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

import com.exactprosystems.webchannels.web.BinaryMessageHandler;
import com.exactprosystems.webchannels.web.TextMessageHandler;

public class WebSocketChannelProcessor extends AbstractChannelProcessor{
	
	public WebSocketChannelProcessor(AbstractHandlerFactory handlerFactory, AbstractMessageFactory messageFactory, ChannelSettings settings) {
		
		super(handlerFactory, messageFactory, settings, 
				new WebSocketChannelFactory(messageFactory, handlerFactory));
		
	}
	
	public void processWSRequest(Session session, EndpointConfig config) {
		
		String channelId = session.getRequestParameterMap().get("channelId").get(0);
		
		if (channelId == null) {
			throw new RuntimeException("Requested channelId is not defined");
		}
		
		AbstractChannel channel = channels.get(channelId);
		
		if (channel == null) {
			HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
			ChannelSettings settings = getSettings(httpSession, null);
			channel = channelFactory.createChannel(channelId, settings, executor, httpSession);
			AbstractChannel prev = channels.putIfAbsent(channel.getID(), channel);
			if (prev != null) {
				channel.close();
				channel = prev;
			} else {
				channel.initHandler();
				SessionContrtoller.getInstance().registerChannel(channel, httpSession);
			}
		}
		
		logger.trace("Process WebSocket {} for {}", session, channel);
		
		if (channel.getChannelSettings().isCompressionEnabled()) {
			BinaryMessageHandler messageHandler = new BinaryMessageHandler(messageFactory, channel);
			session.addMessageHandler(messageHandler);
			channel.bind(session);
		} else {
			TextMessageHandler messageHandler = new TextMessageHandler(messageFactory, channel);
			session.addMessageHandler(messageHandler);
			channel.bind(session);
		}
		
	}
	
	public void processWSClose(Session session, CloseReason reason) {
		
		String channelId = session.getRequestParameterMap().get("channelId").get(0);
		
		if (channelId == null) {
			throw new RuntimeException("Requested channelId is not defined");
		}
		
		AbstractChannel channel = channels.get(channelId);
		
		if (channel != null) {
			logger.trace("Close WebSocket {} for {} with reason {}", session, channel, reason);
			channel.unbind(session);
		}
		
	}
	
	private ChannelSettings getSettings(HttpSession session, HandshakeRequest request) {
		return new ChannelSettings(
					getSaveValue((Long) session.getAttribute("pollingInterval"), settings.getPollingInterval()), 
					getSaveValue((Long) session.getAttribute("heartbeatInterval"), settings.getHeartBeatInterval()), 
					settings.getMaxCountToSend(), 
					settings.getExecutorBatchSize(), 
					getSaveValue((Long) session.getAttribute("sessionDestroyInterval"), settings.getDisconnectTimeout()),
					settings.getThreadCount(),
					settings.getResendBufferSize(),
					getSaveValue((Boolean) session.getAttribute("compressionEnabled"), settings.isCompressionEnabled()));
	}

	private <T> T getSaveValue(T value, T defaultValue) {
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}
	
	@Override
	public String toString() {
		return "WebSocketChannelProcessor[]";
	}
	
}
