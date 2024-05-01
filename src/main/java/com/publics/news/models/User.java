package com.publics.news.models;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

/**
 * Generating table and it's column for user
 *
 */
@Entity
@Table(name = "userdata")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "Username")
	private String username;

	@Column(name = "Password")
	private String password;

	@Column(name = "Email")
	private String email;

	@Column(name = "Department")
	private String depart;
	
	@Column(name = "otp")
	private String otp;
	
    @Column(name = "salt")
    private String salt;
    
    @Column(name = "Userverified", columnDefinition="boolean default false")
    private boolean userverified;
    
    @Transient
	private String jwtToken;

}
