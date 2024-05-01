package com.publics.news.wrapper;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AddUserWrapper {

	@NotEmpty(message = "Username must not be empty")
	private String username;

	@NotEmpty(message = "Password must not br empty")
	private String password;
	
	@NotEmpty(message = "Email must not be empty")
	@Email(message = "Email is not valid")
	private String email;

	@NotEmpty(message = "Department must not be empty")
	private String depart;
	
}
