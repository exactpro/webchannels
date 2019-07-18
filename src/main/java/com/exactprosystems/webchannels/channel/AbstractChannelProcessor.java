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

import com.exactprosystems.webchannels.enums.ChannelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

public abstract class AbstractChannelProcessor implements HttpSessionListener {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final ConcurrentMap<String, AbstractChannel> channels;
	
	protected final AbstractHandlerFactory handlerFactory;
	
	protected final AbstractChannelFactory channelFactory;
	
	protected final AbstractMessageFactory messageFactory;
	
	protected final ChannelSettings settings;
	
	protected final Executor executor;
	
	protected final IdleChannelsMonitor idleChannelsMonitor;

	private final Thread idleChannelsMonitorThread;
	
	public AbstractChannelProcessor(AbstractHandlerFactory handlerFactory, AbstractMessageFactory messageFactory,
			ChannelSettings settings, AbstractChannelFactory channelFactory, Executor executor) {
		
		this.channels = new ConcurrentHashMap<String, AbstractChannel>();
		this.settings = settings;
		this.handlerFactory = handlerFactory;
		this.messageFactory = messageFactory;
		this.channelFactory = channelFactory;
		this.executor = executor;
		
		this.idleChannelsMonitor = new IdleChannelsMonitor();
		this.idleChannelsMonitorThread = new Thread(idleChannelsMonitor, this.getClass().getSimpleName() + "-IdleChannelsMonitor");
		this.idleChannelsMonitorThread.start();
		
		logger.info("Create processor {}", this);
		
	}
	
	public AbstractHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public AbstractChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public AbstractMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public ChannelSettings getSettings() {
		return settings;
	}

	protected static <T> T getSaveValue(T ...values) {
		for (T value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
				
	}
	
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		SessionContrtoller.getInstance().destroySessionChannels(session);
	}
	
	public void close(HttpSession session) {
		SessionContrtoller.getInstance().destroySessionChannels(session);
	}
	
	public void destroy() {
		
		for (AbstractChannel channel : channels.values()) {
			channel.close();
		}
		
		channels.clear();
		
		idleChannelsMonitor.stop();
		try {
			idleChannelsMonitorThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		
		logger.info("Destroy processor {}", this);
		
	}

	private class IdleChannelsMonitor implements Runnable {

		private volatile boolean running = true;

		public void stop() {
			running = false;
		}
		
		@Override
		public void run() {
			
			while (running && !Thread.currentThread().isInterrupted()) {
				
				try {
					
					List<AbstractChannel> toDelete = new ArrayList<AbstractChannel>();
					
					for (Entry<String, AbstractChannel> entry : channels.entrySet()) {
						AbstractChannel channel = entry.getValue();
						if (channel.getStatus() != ChannelStatus.CLOSED) {
							channel.schedule();
						} else {
							toDelete.add(channel);
						}
					}
					
					for (AbstractChannel channel : toDelete) {
						channels.remove(channel.getID());
						SessionContrtoller.getInstance().unregisterChannel(channel);
					}
				
				} catch (Throwable e) {
					
					logger.error(e.getMessage(), e);
					
				}
					
				try {
					Thread.sleep(settings.getPollingInterval());
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
					running = false;
				}
				
			}
			
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("IdleChannelsMonitor [running=");
			builder.append(running);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
}
