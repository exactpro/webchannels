package com.exactprosystems.webchannels;

/**
 * 
 * Basic interface to define updates retriever which
 * contain collection of {@link IUpdateRequestListener}
 * 
 * @author dmitry.zavodchikov
 *
 */
public interface IUpdateRetriever {
	
	void registerUpdateRequest(IUpdateRequestListener listener);
	
	void unregisterUpdateRequest(IUpdateRequestListener listener);
	
	void synchronizeUpdateRequest(IUpdateRequestListener listener);
	
	void destroy();
	
}
