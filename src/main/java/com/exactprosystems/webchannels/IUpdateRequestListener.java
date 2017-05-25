package com.exactprosystems.webchannels;

import com.exactprosystems.webchannels.enums.ServerStatus;
import com.exactprosystems.webchannels.exceptions.IncorrectEventTypeException;

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
	void onEvent(Object event) throws IncorrectEventTypeException;
	
	/**
	 * Subscriber status event handler
	 * @param status {@link ServerStatus}
	 */
	@Deprecated
	void onStatusChange(ServerStatus status);
	
	/**
	 * Subscriber destroy event handler
	 */
	void destroy();
	
	/**
	 * Return event filter {@link IUpdateFilter}
	 * @return {@link IUpdateFilter}
	 */
	@Deprecated
	IUpdateFilter getFilter();
	
	/**
	 * Get unique identifier of subscriber (defines on client side)
	 * @return id
	 */
	String getId();
	
}





