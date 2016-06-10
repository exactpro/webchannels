package com.exactprosystems.webchannels.exceptions;

public class DecodingException extends Exception {

	private static final long serialVersionUID = 1L;

	public DecodingException(String msg) {
		super(msg);
	}
	
	public DecodingException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
