package com.publics.news.wrapper;

import lombok.Data;

@Data
public class UserDataJWTWrapper {

	private int id;

	private String username;

	private String email;

	private String depart;
	
	private String jwtToken;
	
}

