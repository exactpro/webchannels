package com.exactprosystems.ticker;

import com.exactprosystems.ticker.enums.ServerStatus;
import com.exactprosystems.ticker.exceptions.IncorrectEventTypeException;

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
	void onEvent(AbstractUpdateEvent event) throws IncorrectEventTypeException;
	
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
	IUpdateFilter getFilter();
	
	/**
	 * Get unique identifier of subscriber (defines on client side)
	 * @return id
	 */
	String getId();
	
}





