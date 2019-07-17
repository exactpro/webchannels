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
import java.util.concurrent.Executor;

/**
 * 
 * @author dmitry.zavodchikov
 *
 */
public abstract class AbstractChannelFactory {
	
	private final AbstractMessageFactory messageFactory;
	
	private final AbstractHandlerFactory handlerFactory;
	
	public AbstractChannelFactory(AbstractMessageFactory messageFactory,
                                  AbstractHandlerFactory handlerFactory) {
		this.messageFactory = messageFactory;
		this.handlerFactory = handlerFactory;
	}
	
	public AbstractMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public AbstractHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public abstract AbstractChannel createChannel(String channelId, ChannelSettings settings, Executor executor, HttpSession httpSession);
	
}
