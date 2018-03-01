package com.tigerfixonline.crud.exception;

public class CRUDException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3866775177215757258L;
	private String message;
	private Throwable cause;

	public CRUDException() {

	}

	public CRUDException(String message) {
		super(message);
		this.message = message;
	}

	public CRUDException(String message, Throwable cause) {
		super(message);
		initCause(cause);
		this.message = message;
		this.cause = cause;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return cause;
	}
	

}
