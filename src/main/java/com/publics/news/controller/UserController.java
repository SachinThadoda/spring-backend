package com.publics.news.controller;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.publics.news.exceptions.InvalidException;
import com.publics.news.models.User;
import com.publics.news.repositories.StudentRepo;
import com.publics.news.service.UserService;
import com.publics.news.util.Messages;
import com.publics.news.util.Utils;
import com.publics.news.wrapper.AddUserWrapper;
import com.publics.news.wrapper.PagingWrapper;
import com.publics.news.wrapper.UpdateUserWrapper;
import com.publics.news.wrapper.UserDataWrapper;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private StudentRepo studentRepo;

	/**
	 * This API is used for getting all user data.
	 * 
	 * @return
	 */
	@GetMapping("/all-user")
	public ResponseEntity<List<UserDataWrapper>> getAllUser() throws InvalidException {

		int id = Utils.getJwtUserId();
		User user = userService.getUserById(id);

		if (!user.getEmail().equals("sachinthadoda1729@gmail.com")) {
			throw new InvalidException("faild");
		}

		return ResponseEntity.ok(userService.getAllUser());
	}

	/**
	 * This API is used for getting single user data by ID.
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/")
	public ResponseEntity<User> getSingleUser() {

		int id = Utils.getJwtUserId();

		return ResponseEntity.ok(this.userService.getUserById(id));
	}

	/**
	 * This API is used for creating new user.
	 * 
	 * @param userWrapper
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody AddUserWrapper addUserWrapper)
			throws MessagingException {
		Map<String, Object> hm = userService.createUser(addUserWrapper);
		return new ResponseEntity<>(hm, HttpStatus.CREATED);
	}

	/**
	 * This API is used for updating user data
	 * 
	 * @param userWrapper
	 * @param userId
	 * @return
	 */
	@PutMapping("/")
	public ResponseEntity<UserDataWrapper> updateUser(@Valid @RequestBody UpdateUserWrapper updateuserWrapper) {
		int id = Utils.getJwtUserId();
		UserDataWrapper updatedUser = userService.updateUser(updateuserWrapper, id);
		return ResponseEntity.ok(updatedUser);
	}

	/**
	 * This API is used for deleting user data
	 * 
	 * @param userId
	 * @return
	 */
	@DeleteMapping("/")
	public ResponseEntity<Object> deleteUser() {
		int id = Utils.getJwtUserId();
		userService.deleteUser(id);
		return new ResponseEntity<>(Messages.USER_DELETED_SUCCESSFULLY, HttpStatus.OK);
	}

	/**
	 * This API is used for user login
	 * 
	 * @param userWrapper
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestParam("password") String pass,
			@RequestParam("email") String email) {
		Map<String, Object> hm = userService.login(pass, email);
		return new ResponseEntity<>(hm, HttpStatus.OK);
	}

	/**
	 * This API is used for forgot password
	 * 
	 * @param userWrapper
	 * @return
	 */
	@PostMapping("/forgotpassword")
	public ResponseEntity<Map<String, Object>> forgetPassword(@RequestParam String email) throws MessagingException {
		Map<String, Object> hm = userService.forgetPassword(email);
		return new ResponseEntity<>(hm, HttpStatus.OK);
	}

	/**
	 * This API is used for Changing Password of User
	 * 
	 * @param changepassWrapper
	 * @return
	 */
	@PostMapping("/changepassword")
	public ResponseEntity<Object> changePassword(@RequestParam String oldpass, @RequestParam String newpass) {
		userService.changePassword(oldpass, newpass);
		return new ResponseEntity<>(Messages.PASSWORD_CHANGED_SUCCESSFULLY, HttpStatus.OK);
	}

	/**
	 * This API is used for OTP verification
	 * 
	 * @param otpVeriWrapper
	 * @return
	 */
	@PostMapping("/otpverification")
	public ResponseEntity<Map<String, Object>> otpverification(@RequestParam String email, @RequestParam String otp) {
		Map<String, Object> hm =  userService.otpverification(email, otp);
		return new ResponseEntity<>(hm, HttpStatus.OK);

	}

	/**
	 * This API is used for changing password with verified OTP
	 * 
	 * @param newpass
	 * @param email
	 * @return
	 */
	@PostMapping("/changepassotp")
	public ResponseEntity<Map<String, Object>> changepassotp(@RequestParam String newpass, @RequestParam String email) {
		Map<String, Object> hm = userService.changePassOtp(newpass, email);
		return new ResponseEntity<>(hm, HttpStatus.OK);
	}

	/**
	 * This API is used for viewing data with pagination
	 * 
	 * @param pagingWrapper
	 * @return
	 */
	@PostMapping("/paging")
	public ResponseEntity<List<UserDataWrapper>> getAllUsersByPaging(@RequestBody PagingWrapper pagingWrapper) {
		int id = Utils.getJwtUserId();
		User user = userService.getUserById(id);

		if (!user.getEmail().equals("sachinthadoda1729@gmail.com")) {
			throw new InvalidException("faild");
		} else
			return ResponseEntity.ok(this.userService.getAllUserByPaging(pagingWrapper));

	}

	@PostMapping("/jasper")
	public String createJasper() {

		return userService.createJasper("Sachin", "Monarch", 1236487);
	}
}
