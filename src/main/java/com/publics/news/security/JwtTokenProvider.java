package com.publics.news.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.publics.news.util.Messages;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component

public class JwtTokenProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs;

	/**
	 * This function is used to generate JWT token.
	 * 
	 * @param authentication
	 * @return JWT token
	 */
	public String generateJwtToken(Authentication authentication) {

		Date now = new Date();
		Date exp = new Date(System.currentTimeMillis() + jwtExpirationInMs);
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		return Jwts.builder().setSubject(Integer.toString(userPrincipal.getId())).setIssuedAt(now).setExpiration(exp)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();

	}

	/**
	 * This function is used to get user email from JWT token.
	 * 
	 * @param token
	 * @return
	 */
	public String getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	/**
	 * This function is used to validate JWT token.
	 * 
	 * @param authToken token to validate
	 * @return
	 */
	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error(Messages.CONTEXT, ex);
		} catch (MalformedJwtException ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error(Messages.CONTEXT, ex);
		} catch (ExpiredJwtException ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error(Messages.CONTEXT, ex);
		} catch (UnsupportedJwtException ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error(Messages.CONTEXT, ex);
		} catch (IllegalArgumentException ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error(Messages.CONTEXT, ex);
		}
		return false;
	}

}
