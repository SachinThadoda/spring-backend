package com.publics.news.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.publics.news.models.User;
import com.publics.news.security.JwtTokenProvider;
import com.publics.news.security.UserPrincipal;
import com.publics.news.service.CustomUserDetailsServices;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	private static final SecureRandom secureRandom = new SecureRandom();
	private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

	/**
	 * This method is used to generate 6 Digit OTP
	 * 
	 * @return
	 */
	public static String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	/**
	 * This method is used for generate random token
	 * 
	 * @return encoded generated token
	 */
	public static String generateNewToken() {
		byte[] randomBytes = new byte[12];
		secureRandom.nextBytes(randomBytes);
		return base64Encoder.encodeToString(randomBytes);
	}

	public static final void printLog(String tag, String message) {
		LOGGER.info(String.format("%s : %s", tag, message));
	}

	/**
	 * This method is used to get jwt token.
	 * 
	 * @param user                    {@link User}
	 * @param customUserDetailService {@link CustomUserDetailsServices}
	 * @param jwtTokenProvider        {@link JwtTokenProvider}
	 * @param bCryptPasswordEncoder   {@link PasswordEncoder}
	 * @return jwt token
	 */
	public static String getJwtToken(User user, CustomUserDetailsServices customUserDetailService,
			JwtTokenProvider jwtTokenProvider, PasswordEncoder bCryptPasswordEncoder) {

		UserDetails userDetails = customUserDetailService.loadUserById(user.getId());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
				bCryptPasswordEncoder.encode(user.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		return jwtTokenProvider.generateJwtToken(authentication);
	}

	/**
	 * This method is used to get user id for Jwt Token.
	 * 
	 * @return {@link User}
	 */
	public static int getJwtUserId() {

		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return user.getId();

	}

	/**
	 * This method is used to generate password
	 * 
	 * @param user
	 * @return
	 */
	public static String[] generatePassword(String password) {
		String generatedPassword = null;
		byte[] salt = new byte[16];

		String[] str = new String[2];
		try {
			salt = getSalt();
			str[0] = Base64.getEncoder().encodeToString(salt);
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt);
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
			str[1] = generatedPassword;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * This method is used to generate salt for password encryption
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	}

}
