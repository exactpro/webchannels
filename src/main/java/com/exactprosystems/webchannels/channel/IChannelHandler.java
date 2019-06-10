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

import com.exactprosystems.webchannels.messages.AbstractMessage;

/**
 * 
 * Interface which define business logic of channels framework
 * 
 * @author dmitry.zavodchikov
 *
 */
public interface IChannelHandler {
	
	/**
	 * {@link AbstractChannel} create event handler
	 * @param channel {@link AbstractChannel}
	 */
	void onCreate(AbstractChannel channel);
	
	/**
	 * {@link AbstractChannel} message receive event handler
	 * @param message {@link AbstractMessage}
	 * @return response
	 */
	AbstractMessage onReceive(AbstractMessage message, long seqnum);
	
	/**
	 *  {@link AbstractChannel} message send event handler
	 * @param message {@link AbstractMessage}
	 */
	void onSend(AbstractMessage message, long seqnum);
	
	/**
	 * {@link AbstractChannel} close event handler
	 */
	void onClose();

	/**
	 * {@link AbstractChannel} exception event handler
	 * @param t {@link Exception} exception
	 */
	void onException(Throwable t);
	
	/**
	 * {@link AbstractChannel} onIdle event handler
	 */
	void onIdle();

}
