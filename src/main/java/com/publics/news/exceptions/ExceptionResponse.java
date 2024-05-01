package com.publics.news.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ExceptionResponse {

	private String message;
	private int status;

	public ExceptionResponse(String message, HttpStatus status) {
		this.message = message;
		this.status = status.value();
	}
}