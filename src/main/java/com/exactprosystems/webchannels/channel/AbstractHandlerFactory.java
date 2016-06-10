package com.exactprosystems.webchannels.channel;

/**
 * Abstract class for creating concrete instances of {@link IChannelHandler}
 * 
 * @author dmitry.zavodchikov
 *
 */
public abstract class AbstractHandlerFactory {

	/**
	 * Create concrete instance of {@link IChannelHandler} depending on factory implementation 
	 * @return {@link IChannelHandler}
	 */
	public abstract IChannelHandler createHandler();
	
}
