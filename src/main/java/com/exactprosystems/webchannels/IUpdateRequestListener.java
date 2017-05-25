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





