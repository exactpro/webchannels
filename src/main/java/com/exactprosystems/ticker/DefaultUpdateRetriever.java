package com.exactprosystems.ticker;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.exactprosystems.ticker.exceptions.IncorrectListenerTypeException;

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
	public void registerUpdateRequest(IUpdateRequestListener listener) throws IncorrectListenerTypeException {
		lock.writeLock().lock();
		try {
			if (listener.getClass() == clazz) {
				listeners.add((T) listener);
			} else {
				throw new IncorrectListenerTypeException("Listener type is " + listener.getClass());
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void unregisterUpdateRequest(IUpdateRequestListener listener) throws IncorrectListenerTypeException {
		lock.writeLock().lock();
		try {
			if (listener.getClass() == clazz) {
				listeners.remove(listener);
			} else {
				throw new IncorrectListenerTypeException("Listener type is " + listener.getClass());
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
