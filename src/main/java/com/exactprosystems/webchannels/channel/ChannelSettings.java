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

public class ChannelSettings {

	private final long pollingInterval;
	private final long heartBeatInterval;
	private final long disconnectTimeout;
	private final int maxCountToSend;
	private final int executorBatchSize;
	private final int threadCount;
	private final int resendBufferSize;
	private final boolean compressionEnabled;
	
	public ChannelSettings() {
		this.pollingInterval = 1000;
		this.heartBeatInterval = 5000;
		this.maxCountToSend = 250;
		this.executorBatchSize = 10;
		this.disconnectTimeout = 10000;
		this.resendBufferSize = 1024;
		this.compressionEnabled = false;
		this.threadCount = Runtime.getRuntime().availableProcessors();
	}
	
	public ChannelSettings(long pollingInterval, long heartBeatInterval, 
			int maxCountToSend, int executorBatchSize, long disconnectTimeout,
			int threadCount, int resendBufferSize, boolean compressionEnabled) {
		this.pollingInterval = pollingInterval;
		this.heartBeatInterval = heartBeatInterval;
		this.maxCountToSend = maxCountToSend;
		this.executorBatchSize = executorBatchSize;
		this.disconnectTimeout = disconnectTimeout;
		this.threadCount = threadCount;
		this.resendBufferSize = resendBufferSize;
		this.compressionEnabled = compressionEnabled;
	}

	public long getPollingInterval() {
		return pollingInterval;
	}
	
	public long getHeartBeatInterval() {
		return heartBeatInterval;
	}
	
	public long getDisconnectTimeout() {
		return disconnectTimeout;
	}

	public int getMaxCountToSend() {
		return maxCountToSend;
	}

	public int getExecutorBatchSize() {
		return executorBatchSize;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public int getResendBufferSize() {
		return resendBufferSize;
	}

	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelSettings [pollingInterval=");
		builder.append(pollingInterval);
		builder.append(", heartBeatInterval=");
		builder.append(heartBeatInterval);
		builder.append(", maxCountToSend=");
		builder.append(maxCountToSend);
		builder.append(", executorBatchSize=");
		builder.append(executorBatchSize);
		builder.append(", sessionDestroyInterval=");
		builder.append(disconnectTimeout);
		builder.append(", threadCount=");
		builder.append(threadCount);
		builder.append(", resendBufferSize=");
		builder.append(resendBufferSize);
		builder.append("]");
		return builder.toString();
	}

	
	
}
