package com.exactprosystems.ticker.channel;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.ticker.enums.ChannelStatus;

public abstract class AbstractChannelProcessor implements HttpSessionListener {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final ConcurrentMap<String, AbstractChannel> channels;
	
	protected final AbstractHandlerFactory handlerFactory;
	
	protected final AbstractChannelFactory channelFactory;
	
	protected final AbstactMessageFactory messageFactory;
	
	protected final ChannelSettings settings;
	
	protected final ExecutorService executor;
	
	protected final IdleChannelsMonitor idleChannelsMonitor;
	
	public AbstractChannelProcessor (AbstractHandlerFactory handlerFactory, AbstactMessageFactory messageFactory,
			ChannelSettings settings, AbstractChannelFactory channelFactory) {
		
		this.channels = new ConcurrentHashMap<String, AbstractChannel>();
		this.settings = settings;
		this.handlerFactory = handlerFactory;
		this.messageFactory = messageFactory;
		this.channelFactory = channelFactory;
		
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("Uncaught exception in channels executor service", e);
			}
		};
		
		this.executor = new ForkJoinPool(settings.getThreadCount(), 
				ForkJoinPool.defaultForkJoinWorkerThreadFactory, handler, false);
		
		this.idleChannelsMonitor = new IdleChannelsMonitor();
		Thread thread = new Thread(idleChannelsMonitor, this.getClass().getSimpleName() + "-IdleChannelsMonitor");
		thread.start();
		
		logger.info("Create processor {}", this);
		
	}
	
	public AbstractHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public AbstractChannelFactory getChannelFactory() {
		return channelFactory;
	}

	public AbstactMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public ChannelSettings getSettings() {
		return settings;
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
			SessionContrtoller.getInstance().unregisterChannel(channel);
		}
		
		idleChannelsMonitor.stop();
		channels.clear();
		executor.shutdownNow();
		
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
