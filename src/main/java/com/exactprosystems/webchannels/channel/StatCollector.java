package com.exactprosystems.webchannels.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.webchannels.enums.ChannelStatus;

public class StatCollector {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final long THREAD_SLEEP_TIME = 100L;
	
	private static final long SECOND = 1000L;
	
	private static final long STAT_TIMEOUT = 60 * SECOND;
	
	private static volatile StatCollector collector;

	private final BlockingQueue<ChannelStats> statsQueue;
	
	private final Map<String, ChannelStats> statsMap;
	
	private final Map<String, Long> timeoutMap;
	
	private StatCollectorProcessor statsProcessor;
	
	private StatCleaner cleaner;
	
	private final ReentrantLock lock;
	
	private volatile boolean started;

	private Thread processorThread;

	private Thread cleanerThread;
	
	private StatCollector() {
		this.statsQueue = new LinkedBlockingQueue<ChannelStats>();
		this.statsMap = new HashMap<String, ChannelStats>();
		this.timeoutMap = new HashMap<String, Long>();
		this.started = false;
		this.lock = new ReentrantLock();
	}
	
	public static StatCollector getInstance() {
		if (collector == null) {
			synchronized (StatCollector.class) {
				if (collector == null) {
					collector = new StatCollector();
				}
			}
		}
		return collector;
	}
	
	public ChannelStats getStatistics(AbstractChannel channel) {
		lock.lock();
		try {
			return statsMap.get(channel.getID());
		} finally {
			lock.unlock();
		}
	}
	
	public void pubStatistics(AbstractChannel channel, ChannelStats stats) {
		if (started) {
			statsQueue.offer(stats);
		}
	}

	public void start() {
		synchronized (this) {
			if (started == false) {
				statsProcessor = new StatCollectorProcessor();
				processorThread = new Thread(statsProcessor, "Channels-stats-processor");
				processorThread.start();
				cleaner = new StatCleaner();
				cleanerThread = new Thread(cleaner, "Channels-stats-cleaner");
				cleanerThread.start();
				started = true;
			}
		}
	}
	
	public void stop() {
		synchronized (this) {
			if (started == true) {
				statsProcessor.stop();
				try {
					processorThread.join(SECOND);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
				cleaner.stop();
				try {
					cleanerThread.join(SECOND);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
				started = false;
			}
		}
	}
	
	@Override
	public String toString() {
		return "StatCollector []";
	}
	
	private class StatCollectorProcessor implements Runnable {

		private volatile boolean running = true;
		
		public void stop() {
			running = false;
		}
		
		@Override
		public void run() {
			
			while (running && !Thread.interrupted()) {
				
				try {
				
					ChannelStats stats = statsQueue.poll(THREAD_SLEEP_TIME, TimeUnit.MILLISECONDS);
					
					if (stats != null) {
						lock.lock();
						try {
							ChannelStats curStats = statsMap.get(stats.getChannelId());
							if (curStats == null || curStats.getStatus() != ChannelStatus.CLOSED) {
								statsMap.put(stats.getChannelId(), stats);
							}
							if (stats.getStatus() == ChannelStatus.CLOSED) {
								long currentMillis = System.currentTimeMillis();
								timeoutMap.put(stats.getChannelId(), currentMillis);
							}
						} finally {
							lock.unlock();
						}
					}
					
				} catch (InterruptedException e) {
					running = false;
					logger.error(e.getMessage(), e);
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
				
			}
			
		}
		
	}
	
	private class StatCleaner implements Runnable {
		
		private volatile boolean running = true;
		
		public void stop() {
			running = false;
		}
		
		@Override
		public void run() {
			
			while (running && !Thread.interrupted()) {
				
				try {
					
					long currentMillis = System.currentTimeMillis();
					List<String> toRemove = new ArrayList<String>();
					
					lock.lock();
					try {
						for (Entry<String, Long> entry : timeoutMap.entrySet()) {
							long closedTimestamp = entry.getValue();
							if (currentMillis - closedTimestamp > STAT_TIMEOUT) {
								String channelId = entry.getKey();
								statsMap.remove(channelId);
								toRemove.add(channelId);
							}
						}
						for (String key : toRemove) {
							timeoutMap.remove(key);
						}
					} finally {
						lock.unlock();
					}
					
					Thread.sleep(THREAD_SLEEP_TIME);
				
				} catch (InterruptedException e) {
					running = false;
					logger.error(e.getMessage(), e);
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				}
				
			}
			
		}
		
	}
	
}
