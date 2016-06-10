package com.exactprosystems.ticker.exceptions;

public class IncorrectEventTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IncorrectEventTypeException(String msg) {
		super(msg);
	}
	
}
