package com.publics.news.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class InvalidException extends RuntimeException {

	private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	public InvalidException(String message) {
		super(message);
	}

	public InvalidException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public InvalidException(String message, Throwable throwable) {
		super(message, throwable);
	}
	

}

