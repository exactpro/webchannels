package com.exactprosystems.ticker.exceptions;

public class EncodingException extends Exception {

	private static final long serialVersionUID = 1L;

	public EncodingException(String msg) {
		super(msg);
	}

	public EncodingException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
