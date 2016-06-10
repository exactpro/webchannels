package com.exactprosystems.ticker.channel;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionTask.class);
	
	private final AbstractChannel channel;
	
	private final ExecutorService executor;
	
	public ExecutionTask(AbstractChannel channel, ExecutorService executor) {
		this.channel = channel;
		this.executor = executor;
	}
	
	@Override
	public void run() {
	
		int count = 0;
		
		while (!channel.isTaskQueueEmpty() && count < channel.getChannelSettings().getExecutorBatchSize()) {
			count++;
			try {
				channel.processTaskQueue();
			} catch (Throwable e) {
				logger.error("Error during process channel task queue", e);
			}
		}
		
		if (!channel.isTaskQueueEmpty()) {
			executor.submit(new ExecutionTask(channel, executor));
		} else {
			channel.finishProcessing();
		}
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExecutionTask[]");
		return builder.toString();
	}

}
