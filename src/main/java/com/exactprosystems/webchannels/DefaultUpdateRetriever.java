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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class DefaultUpdateRetriever<T extends IUpdateRequestListener> implements IUpdateRetriever {

	private final Set<T> listeners;
	
	private final ReentrantReadWriteLock lock;
	
	private final Class<T> clazz;
	
	public DefaultUpdateRetriever(Class<T> clazz) {
		this.clazz = clazz;
		this.listeners = new HashSet<T>();
		this.lock = new ReentrantReadWriteLock();
	}
	
	@Override
	public void registerUpdateRequest(IUpdateRequestListener listener) {
		lock.writeLock().lock();
		try {
			if (listener.getClass() == clazz) {
				listeners.add((T) listener);
			} else {
				throw new RuntimeException("Listener type is " + listener.getClass());
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterUpdateRequest(IUpdateRequestListener listener) {
		lock.writeLock().lock();
		try {
			if (listener.getClass() == clazz) {
				listeners.remove(listener);
			} else {
				throw new RuntimeException("Listener type is " + listener.getClass());
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	protected Set<T> getListeners() {
		lock.readLock().lock();
		try {
			return new HashSet<T>(listeners);
		} finally {
			lock.readLock().unlock();
		}
	}
	
}
