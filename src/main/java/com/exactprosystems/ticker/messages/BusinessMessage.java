package com.exactprosystems.ticker.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BusinessMessage extends AbstractMessage {

	@JsonIgnore
	@Override
	public boolean isAdmin() {
		return false;
	}
	
}
