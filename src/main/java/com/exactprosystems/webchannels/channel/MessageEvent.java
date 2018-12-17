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

public class MessageEvent {

	private final Object message;
	
	private final Direction direction;

	public MessageEvent(Object message, Direction direction, Object context) {
		super();
		this.message = message;
		this.direction = direction;
	}

	public Object getMessage() {
		return message;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageWrapper[message=");
		builder.append(message);
		builder.append(",direction=");
		builder.append(direction);
		builder.append("]");
		return builder.toString();
	}
	
}
