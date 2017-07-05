package com.exactprosystems.webchannels.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Deprecated
public abstract class BusinessMessage extends AbstractMessage {

	@JsonIgnore
	@Override
	public boolean isAdmin() {
		return false;
	}
	
}
