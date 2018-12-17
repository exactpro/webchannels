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
