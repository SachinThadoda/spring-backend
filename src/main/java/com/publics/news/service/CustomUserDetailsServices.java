package com.publics.news.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.publics.news.models.User;
import com.publics.news.repositories.UserRepository;
import com.publics.news.security.UserPrincipal;



/**
 * Implementation class of customUser details for JWT token.
 * 
 *
 */
@Component
public class CustomUserDetailsServices implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * This is default constructor {@link CustomUserDetailsServices}
	 */
	public CustomUserDetailsServices() {
		// default constructor
	}

	/**
	 * This method is used to get user details by email for authentication purpose
	 * And it's used by JWTAuthenticationFilter
	 * 
	 * This method is default method of {@link UserDetailsService} Which take
	 * request param as string. So that we again make query to get
	 * {@link UserPrincipal} by id.
	 */

	@Transactional
	@Override
	public UserPrincipal loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		return UserPrincipal.createUser(user.getId());
	}

	/**
	 * This method is used to get user details by id for authentication purpose.
	 * 
	 * @param id id of user
	 * @return {@link UserDetails}
	 * @throws UsernameNotFoundException {@link UsernameNotFoundException}
	 */
	@Transactional
	public UserDetails loadUserById(int id) throws UsernameNotFoundException {
		return UserPrincipal.createUser(id);
	}

}
