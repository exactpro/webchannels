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

package com.exactprosystems.webchannels;

/**
 * 
 * Interface to define abstract subscriber
 * 
 * @author dmitry.zavodchikov
 *
 */
public interface IUpdateRequestListener {
	
	/**
	 * Subscriber event {@link AbstractUpdateEvent} handler
	 * @param event {@link AbstractUpdateEvent} event
	 * @throws IncorrectEventTypeException exception
	 */
	void onEvent(Object event);
	
	/**
	 * Subscriber destroy event handler
	 */
	void destroy();
	
	/**
	 * Get unique identifier of subscriber (defines on client side)
	 * @return id
	 */
	String getId();
	
}





