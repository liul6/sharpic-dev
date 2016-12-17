package com.sharpic.common;

public class SharpICException extends Exception {
	private String message = null;

	public SharpICException(String str) {
		message = str;
	}

	public SharpICException(String str, Throwable e) {
		super(str, e);
		message = str;
	}

	public String getMessage() {
		return message;
	}

}
