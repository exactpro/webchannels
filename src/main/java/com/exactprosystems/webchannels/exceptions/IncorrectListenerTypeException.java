package com.exactprosystems.webchannels.exceptions;

@Deprecated
public class IncorrectListenerTypeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public IncorrectListenerTypeException(String msg) {
		super(msg);
	}

}
