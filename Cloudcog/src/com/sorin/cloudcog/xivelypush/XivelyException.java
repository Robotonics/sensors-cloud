package com.sorin.cloudcog.xivelypush;

public class XivelyException extends Exception {

	/**
	 * Error message: This is a HTTP status code retrieved from a failed http
	 * request
	 */
	public String errorMessage;

	public XivelyException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}
}