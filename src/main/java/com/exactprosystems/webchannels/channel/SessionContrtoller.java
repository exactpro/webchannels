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
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.webchannels.IChannelsListener;

public class SessionContrtoller {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static volatile SessionContrtoller mapper;
	
	private final ConcurrentMap<AbstractChannel, HttpSession> sessionMap;
	
	private final CopyOnWriteArrayList<IChannelsListener> listeners;
	
	private SessionContrtoller() {
		sessionMap = new ConcurrentHashMap<AbstractChannel, HttpSession>();
		listeners = new CopyOnWriteArrayList<IChannelsListener>();
		logger.info("Mapped {} instance created", this);
	}
	
	public static SessionContrtoller getInstance() {
		if (mapper == null) {
			synchronized (SessionContrtoller.class) {
				if (mapper == null) {
					mapper = new SessionContrtoller();
				}
			}
		}
		return mapper;
	}
	
	public HttpSession getSession(AbstractChannel channel) {
		return sessionMap.get(channel);
	}
	
	public void registerChannel(AbstractChannel channel, HttpSession session) {
		logger.debug("Register channel {} with session {}", channel, session.getId());
		sessionMap.put(channel, session);
		for (IChannelsListener listener : listeners) {
			try {
				listener.onCreate(channel, session);
			} catch (Exception e) {
				logger.error("Error during onCreate handler of channel {} and session {}", channel, session.getId(), e);
			}
		}
	}
	
	public void unregisterChannel(AbstractChannel channel) {
		HttpSession session = sessionMap.remove(channel);
		if (session != null) {
			logger.debug("Unregister channel {} with session {}", channel, session.getId());
			for (IChannelsListener listener : listeners) {
				try {
					listener.onClose(channel, session);
				} catch (Exception e) {
					logger.error("Error during onClose handler of channel {} and session {}", channel, session.getId(), e);
				}
			}
			boolean isLast = true;
			for (Entry<AbstractChannel, HttpSession> entry : sessionMap.entrySet()) {
				if (entry.getValue().equals(session)) {
					isLast = false;
					break;
				}
			}
			if (isLast) {
				for (IChannelsListener listener : listeners) {
					try {
						listener.onLastClose(session);
					} catch (Exception e) {
						logger.error("Error during onLastClose handler of session {}", session.getId(), e);
					}
				}
			}
		}
	}
	
	public List<AbstractChannel> getChannels(HttpSession session) {
		List<AbstractChannel> channels = new ArrayList<AbstractChannel>();
		for (Entry<AbstractChannel, HttpSession> entry : sessionMap.entrySet()) {
			if (entry.getValue().equals(session)) {
				channels.add(entry.getKey());
			}
		}
		return channels;
	}
	
	public void destroySessionChannels(HttpSession session) {
		logger.debug("Close all channels with session {}", session.getId());
		List<AbstractChannel> channels = new ArrayList<AbstractChannel>();
		for (Entry<AbstractChannel, HttpSession> entry : sessionMap.entrySet()) {
			if (entry.getValue().equals(session)) {
				channels.add(entry.getKey());
			}
		}
		for (AbstractChannel channel : channels) {
			logger.debug("Unregister channel {} with session {}", channel, session.getId());
			sessionMap.remove(channel);
			channel.close();
			for (IChannelsListener listener : listeners) {
				try {
					listener.onClose(channel, session);
				} catch (Exception e) {
					logger.error("Error during onClose handler of channel {} and session {}", channel, session.getId(), e);
				}
			}
		}
		if (channels.isEmpty() == false) {
			for (IChannelsListener listener : listeners) {
				try {
					listener.onLastClose(session);
				} catch (Exception e) {
					logger.error("Error during onLastClose handler of session {}", session.getId(), e);
				}
			}
		}
	}
	
	public void add(IChannelsListener listener) {
		listeners.addIfAbsent(listener);
	}
	
	public void remove(IChannelsListener listener) {
		listeners.remove(listener);
	}

	@Override
	public String toString() {
		return "SessionManager []";
	}
	
}
