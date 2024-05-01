package com.publics.news.exceptions;

/**
 * AuthorizationException is used to throw all types error related to
 * Authorization.
 * 
 * @author xr
 *
 */
public class AuthorizationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	String error;

	public AuthorizationException(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
