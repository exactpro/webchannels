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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exactprosystems.webchannels.enums.ChannelStatus;
import com.exactprosystems.webchannels.messages.AbstractMessage;

/**
 * 
 * Abstract server-side model of connection with JS-client.
 *
 * @author dmitry.zavodchikov
 *
 */
public abstract class AbstractChannel {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String channelId;
	
	private final IChannelHandler handler;
	
	private final Queue<Object> taskQueue;
	
	private final ChannelSettings settings;
	
	private final ExecutorService executor;
	
	private final AtomicBoolean processing;
	
	private final AbstactMessageFactory messageFactory;
	
	private final StatCollector statCollector;
	
	private final HttpSession httpSession;
	
	private volatile ChannelStatus status;

	public AbstractChannel(IChannelHandler handler, String channelId, ChannelSettings settings, 
			AbstactMessageFactory messageFactory, ExecutorService executor, HttpSession httpSession) {
		this.handler = handler;
		this.settings = settings;
		this.taskQueue = new ConcurrentLinkedQueue<Object>();
		this.channelId = channelId;
		this.executor = executor;
		this.messageFactory = messageFactory;
		this.statCollector = StatCollector.getInstance();
		this.processing = new AtomicBoolean(false);
		this.status = ChannelStatus.CREATED;
		this.httpSession = httpSession;
	}
	
	public void initHandler() {
		taskQueue.offer(CreateChannelEvent.getInstance());
		trySubmitExecutionTask();
	}
	
	public void schedule() {
		taskQueue.offer(PollEvent.getInstance());
		trySubmitExecutionTask();
	}
	
	public void bind(Object context) {
		taskQueue.offer(new BindContextEvent(context));
		trySubmitExecutionTask();
	}
	
	public void unbind(Object context) {
		taskQueue.offer(new UnbindContextEvent(context));
		trySubmitExecutionTask();
	}

	public void handleRequest(WithSeqnumWrapper message) {
		taskQueue.offer(new OutputMessageEvent(message.getMessage()));
		trySubmitExecutionTask();
	}

	public void close() {
		taskQueue.offer(CloseChannelEvent.getInstance());
		trySubmitExecutionTask();
	}

	public void sendMessage(AbstractMessage message) {
		taskQueue.offer(new OutputMessageEvent(message));
		trySubmitExecutionTask();
	}
	
	private void trySubmitExecutionTask() {
		if (this.tryProcessing()) {
			executor.submit(new ExecutionTask(this, executor));
		}
	}
	
	protected void processTaskQueue() {
		Object message = taskQueue.poll();
		if (message != null) {
			if (message instanceof OutputMessageEvent) {
                OutputMessageEvent event = (OutputMessageEvent) message;
                processOutputMessage(event.getMessage());
            } else if (message instanceof InputMessageEvent) {
                InputMessageEvent event = (InputMessageEvent) message;
                processInputMessage(event.getMessage());
			} else if (message instanceof CloseChannelEvent) {
				onClose();
				ChannelStats stats = getChannelStats();
				statCollector.pubStatistics(this, stats);
			} else if (message instanceof CreateChannelEvent) {
				onCreate();
				ChannelStats stats = getChannelStats();
				statCollector.pubStatistics(this, stats);
			} else if (message instanceof BindContextEvent) {
				BindContextEvent bindMessage = (BindContextEvent) message;
				onBind(bindMessage.getContext());
			} else if (message instanceof UnbindContextEvent) {
				UnbindContextEvent unbindMessage = (UnbindContextEvent) message;
				onUnbind(unbindMessage.getContext());
			} else if (message instanceof PollEvent) {
				onPoll();
				ChannelStats stats = getChannelStats();
				statCollector.pubStatistics(this, stats);
			} else {
				throw new RuntimeException("Unexpected message: " + message);
			}
		}
	}
	
	protected abstract ChannelStats getChannelStats();

	protected abstract void onUnbind(Object context);

	protected abstract void onPoll();

	protected abstract void processOutputMessage(AbstractMessage message);

	protected abstract void processInputMessage(WithSeqnumWrapper message);

	protected abstract void onClose();

	protected abstract void onCreate();
	
	protected abstract void onBind(Object context);
	
	protected boolean tryProcessing() {
		return processing.compareAndSet(false, true);
	}

	protected void finishProcessing() {
		processing.set(false);
	}

	public IChannelHandler getHandler() {
		return handler;
	}

	public String getID() {
		return channelId;
	}

	public void clearQueue() {
		taskQueue.clear();
	}
	
	public boolean isTaskQueueEmpty() {
		return this.getStatus() == ChannelStatus.CLOSED || taskQueue.isEmpty();
	}

	public void setStatus(ChannelStatus status) {
		if (this.status != ChannelStatus.CLOSED) {
			this.status = status;
		}
	}
	
	public ChannelStatus getStatus() {
		return status;
	}

	public ChannelSettings getChannelSettings() {
		return settings;
	}
	
	public AbstactMessageFactory getMessageFactory() {
		return messageFactory;
	}
	
	public HttpSession getHttpSession() {
		return httpSession;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractChannel other = (AbstractChannel) obj;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		return true;
	}	
	
}
