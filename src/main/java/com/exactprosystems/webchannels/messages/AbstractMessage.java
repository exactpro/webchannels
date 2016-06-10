package com.exactprosystems.webchannels.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * Base message class
 * 
 * @author dmitry.zavodchikov
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="messageType")
public abstract class AbstractMessage {
	
	public abstract boolean isAdmin();
	
}
