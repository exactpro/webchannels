package com.exactprosystems.ticker.channel;

import com.exactprosystems.ticker.messages.AbstractMessage;

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
