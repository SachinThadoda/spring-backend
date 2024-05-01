package com.publics.news.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.publics.news.service.CustomUserDetailsServices;
import com.publics.news.util.Messages;



/**
 * implementation class of jwt authentication filter
 * 
 *
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CustomUserDetailsServices customUserDetailsDaoImpl;

	/**
	 * This function is used to do filter of JWT token and set authentication of
	 * user.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
				String userId = jwtTokenProvider.getUserIdFromJWT(jwt);
				UserDetails userDetails = customUserDetailsDaoImpl.loadUserById(Integer.parseInt(userId));
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(Messages.CONTEXT, e);
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * This function is used to get JWT token as header.
	 * 
	 * @param request
	 * @return jwt token
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return token;
	}

}
