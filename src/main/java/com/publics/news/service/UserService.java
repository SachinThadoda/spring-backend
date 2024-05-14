package com.publics.news.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.publics.news.exceptions.InvalidException;
import com.publics.news.models.HardwareItem;
import com.publics.news.models.ProductDataWrapper;
import com.publics.news.models.User;
import com.publics.news.repositories.UserRepository;
import com.publics.news.security.JwtTokenProvider;
import com.publics.news.util.Messages;
import com.publics.news.util.Utils;
import com.publics.news.wrapper.AddUserWrapper;
import com.publics.news.wrapper.PagingWrapper;
import com.publics.news.wrapper.UpdateUserWrapper;
import com.publics.news.wrapper.UserDataJWTWrapper;
import com.publics.news.wrapper.UserDataWrapper;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private CustomUserDetailsServices customUserDetailsService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private ApplicationContext applicationContext;

	private static DecimalFormat df2 = new DecimalFormat(".##");

	/**
	 * This method is used for creating new user
	 * 
	 * @param userWrapper
	 * @return
	 */
	public Map<String, Object> createUser(AddUserWrapper addUserWrapper) throws MessagingException {

		Map<String, Object> hm = new HashMap<String, Object>();
		if (userRepository.existsByEmail(addUserWrapper.getEmail())) {
			hm.put("success", false);
			hm.put("message", "Email Already Exist!");
			return hm;
		}
		User user = new User();
		BeanUtils.copyProperties(addUserWrapper, user);

		String[] str = new String[2];
		str = Utils.generatePassword(user.getPassword());
		user.setPassword(str[1]);
		user.setSalt(str[0]);

		String otp = Utils.generateOtp();

//		sendEmail(user.getEmail(), otp);
		user.setOtp(otp);
		user.setJwtToken(Utils.getJwtToken(user, customUserDetailsService, jwtTokenProvider, bCryptPasswordEncoder));
		user = userRepository.save(user);
		UserDataJWTWrapper userDataJWTWrapper = new UserDataJWTWrapper();
		BeanUtils.copyProperties(user, userDataJWTWrapper);
		hm.put("success", true);
		hm.put("userData", userDataJWTWrapper);
		return hm;
	}

	/**
	 * This method is used for getting user by their ID
	 * 
	 * @param userId
	 * @return
	 */
	public User getUserById(int userId) throws InvalidException {

		User user = userRepository.findById(userId).orElseThrow(() -> new InvalidException(Messages.USER_NOT_FOUND));

		user.setJwtToken(Utils.getJwtToken(user, customUserDetailsService, jwtTokenProvider, bCryptPasswordEncoder));
		return user;
	}

	/**
	 * This method is used for updating user data
	 * 
	 * @param userWrapper
	 * @param userId
	 * @return
	 */
	public UserDataWrapper updateUser(UpdateUserWrapper updateuserWrapper, int userId) {

		User user = userRepository.findById(userId).orElseThrow(() -> new InvalidException(Messages.USER_NOT_FOUND));

		UserDataWrapper userDataWrapper = new UserDataWrapper();
		BeanUtils.copyProperties(updateuserWrapper, user);
		BeanUtils.copyProperties(user, userDataWrapper);
		user = userRepository.save(user);
		return userDataWrapper;
	}

	/**
	 * This method is used for getting all user data
	 * 
	 * @return
	 */
	public List<UserDataWrapper> getAllUser() {
		List<User> user = userRepository.findAll();
		if (user.isEmpty())
			throw new InvalidException(Messages.NO_DATA_AVAILABLE);
		List<UserDataWrapper> userDataWrapper = new ArrayList<UserDataWrapper>();
		for (User u : user) {
			UserDataWrapper dataWrapper = new UserDataWrapper();
			BeanUtils.copyProperties(u, dataWrapper);
			userDataWrapper.add(dataWrapper);
		}
		return userDataWrapper;
	}

	/**
	 * This method is used for deleting all user data
	 * 
	 * @param userId
	 */
	public void deleteUser(int userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new InvalidException(Messages.USER_NOT_FOUND));

		this.userRepository.delete(user);
	}

	/**
	 * This method is used for login
	 * 
	 * @param userWrapper
	 */
	public Map<String, Object> login(String pass, String email) {

		Map<String, Object> hm = new HashMap<>();
		User user = userRepository.findByEmail(email);
		if (user == null) {
			hm.put("success", false);
			hm.put("message", "User doesn't exist with this email");
			return hm;
		}

		String generatedPassword = EncryptPassword(pass, user.getSalt());
		if ((user.getEmail()).equals(email) && (user.getPassword()).equals(generatedPassword)) {
			user.setJwtToken(
					Utils.getJwtToken(user, customUserDetailsService, jwtTokenProvider, bCryptPasswordEncoder));
		}

		else {
			hm.put("success", false);
			hm.put("message", "Password is wrong");
			return hm;
		}

		hm.put("success", true);
		hm.put("userData", user);
		return hm;

	}

	/**
	 * This method is used for forgot password
	 * 
	 * @param userWrapper
	 * @return
	 */
	public void forgetPassword(String email) throws MessagingException {

		User user = userRepository.findByEmail(email);

		if (user == null)
			throw new InvalidException(Messages.USER_NOT_FOUND_WITH_EMAIL + email);

		String otp = Utils.generateOtp();

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(user.getEmail());
		helper.setSubject("Your OTP to Change Password");

		String emailContent = "<html>" + "<head>" + "<style>"
				+ "body {font-family: Arial, sans-serif; margin: 0; padding: 0;}"
				+ ".container {max-width: 600px; margin: 0 auto; padding: 20px;}"
				+ ".header {background-color: #007bff; color: #fff; padding: 10px;}"
				+ ".header h1 {font-size: 24px; margin: 0;}" + ".content {padding: 20px;}"
				+ "p {font-size: 16px; line-height: 1.5em;}" + "h2 {font-size: 20px; margin-bottom: 10px;}"
				+ ".otp {font-size: 28px; font-weight: bold; color: #007bff;}" + "</style>" + "</head>" + "<body>"
				+ "<div class=\"container\">" + "<div class=\"header\">" + "<h1>User API</h1>" + "</div>"
				+ "<div class=\"content\">" + "<p>Use the OTP given below to change password.</p>" + "<p class=\"otp\">"
				+ otp + "</p>" + "<h2>Thanks & Regards!</h2>" + "</div>" + "</div>" + "</body>" + "</html>";

		helper.setText(emailContent, true);
		emailSender.send(message);

		user.setOtp(otp);
		user = userRepository.save(user);
	}

	/**
	 * This method is used for changing password.
	 * 
	 * @param changepassWrapper
	 */
	public void changePassword(String oldpass, String newpass) {
		int id = Utils.getJwtUserId();
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidException(Messages.USER_NOT_FOUND));

		String Password = EncryptPassword(oldpass, user.getSalt());

		if (Password.equals(user.getPassword())) {

			String[] str = new String[2];
			str = Utils.generatePassword(newpass);
			user.setPassword(str[1]);
			user.setSalt(str[0]);
			userRepository.save(user);
		} else
			throw new InvalidException(Messages.PASSWORD_IS_WRONG);

	}

	/**
	 * This method is used for OTP Verification
	 * 
	 * @param otpVeriWrapper
	 * @return
	 */
	public void otpverification(String email, String otp) {
		User user = userRepository.findByEmail(email);

		if (user == null)
			throw new InvalidException(Messages.USER_NOT_FOUND_WITH_EMAIL + email);

		if ((otp.equals(user.getOtp()) && email.equals(user.getEmail()))) {
			user.setUserverified(true);
			userRepository.save(user);
		} else
			throw new InvalidException(Messages.OTP_DOESNT_VERIFIED);
	}

	/**
	 * This method is used for Changing password with otp
	 * 
	 * @param newpass
	 * @param email
	 */
	public void changePassOtp(String newpass, String email) {
		User user = userRepository.findByEmail(email);

		if (user == null)
			throw new InvalidException(Messages.USER_NOT_FOUND_WITH_EMAIL + email);

		if (user.isUserverified()) {
			String[] str = new String[2];
			str = Utils.generatePassword(newpass);
			user.setPassword(str[1]);
			user.setSalt(str[0]);
			user.setUserverified(false);
			userRepository.save(user);

		} else {
			throw new InvalidException(Messages.VERIFY_YOUR_OTP);
		}
	}

	/**
	 * This method is used to encrypt password
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	public String EncryptPassword(String password, String salt) {
		String generatedPassword = null;
		byte[] salt1 = Base64.getDecoder().decode(salt);

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt1);
			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	/**
	 * This method is used to get all user data with pagination
	 * 
	 * @param pagingWrapper
	 * @return
	 */
	public List<UserDataWrapper> getAllUserByPaging(PagingWrapper pagingWrapper) {

		Pageable paging = PageRequest.of(pagingWrapper.getPage(), pagingWrapper.getSize());

		Page<User> pageResult = userRepository.findAll(paging);
		List<User> users = pageResult.getContent();
		List<UserDataWrapper> userDataWrapper = new ArrayList<UserDataWrapper>();
		for (User u : users) {
			UserDataWrapper dataWrapper = new UserDataWrapper();
			BeanUtils.copyProperties(u, dataWrapper);
			userDataWrapper.add(dataWrapper);
		}
		return userDataWrapper;
	}

	public void sendEmail(String email, String otp) throws MessagingException {

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setTo(email);
		helper.setSubject("Your OTP for login");

		String emailContent = "<html>" + "<head>" + "<style>"
				+ "body {font-family: Arial, sans-serif; margin: 0; padding: 0;}"
				+ ".container {max-width: 600px; margin: 0 auto; padding: 20px;}"
				+ ".header {background-color: #007bff; color: #fff; padding: 10px;}"
				+ ".header h1 {font-size: 24px; margin: 0;}" + ".content {padding: 20px;}"
				+ "p {font-size: 16px; line-height: 1.5em;}" + "h2 {font-size: 20px; margin-bottom: 10px;}"
				+ ".otp {font-size: 28px; font-weight: bold; color: #007bff;}" + "</style>" + "</head>" + "<body>"
				+ "<div class=\"container\">" + "<div class=\"header\">" + "<h1>User API</h1>" + "</div>"
				+ "<div class=\"content\">" + "<h2>You have successfully registered on User API! </h2>"
				+ "<p>Use the OTP given below to verify your email ID.</p>" + "<p class=\"otp\">" + otp + "</p>"
				+ "<h2>Thanks & Regards!</h2>" + "</div>" + "</div>" + "</body>" + "</html>";

		helper.setText(emailContent, true);
		emailSender.send(message);

	}

	public String createJasper(String name, String address, double phone) {

		try {
			Long count = 1L;
//			String obj = "{\r\n" + "  \"username\": \"Sachin\",\r\n" + "  \"address\": \"morbi\"\r\n" + "}";
//			JSONObject object = new JSONObject(obj);
//			System.out.println("Object " + object);
			List<ProductDataWrapper> products = new ArrayList<>();

			for (int i = 0; i < 40; i++) {
				ProductDataWrapper product = new ProductDataWrapper();
				product.setId(count);
				product.setItem("Post");
				product.setLength(22);
				product.setWidth(28);
				product.setThick(1200.0);
				product.setQty(12);
				product.setWeight(87);
				product.setArea(221.21);
				product.setFinish("New Finish");
				product.setRemarks("Remarcks for..");
				product.setType("NONSS");
				products.add(product);
				count++;
			}

			for (int i = 0; i < 4; i++) {
				ProductDataWrapper product = new ProductDataWrapper();
				product.setId(count);
				product.setItem("Post");
				product.setLength(22);
				product.setWidth(28);
				product.setThick(1200.0);
				product.setQty(12);
				product.setWeight(87);
				product.setArea(221.21);
				product.setFinish("New Finish");
				product.setRemarks("Remarcks for..");
				product.setType("NONSS");
				products.add(product);
				count++;
			}

			for (int i = 0; i < 17; i++) {
				ProductDataWrapper product = new ProductDataWrapper();
				product.setId(count);
				product.setItem("Post");
				product.setLength(22);
				product.setWidth(28);
				product.setThick(1200.0);
				product.setQty(12);
				product.setWeight(87);
				product.setArea(221.21);
				product.setFinish("New Finish");
				product.setRemarks("Remarcks for..");
				product.setType("NONSS");
				products.add(product);
				count++;
			}
			for (int i = 0; i < 25; i++) {
				ProductDataWrapper product = new ProductDataWrapper();
				product.setId(null);
				product.setItem("Post");
				product.setLength(22);
				product.setWidth(28);
				product.setThick(1200.0);
				product.setQty(12);
				product.setWeight(87);
				product.setArea(221.21);
				product.setFinish("New Finish");
				product.setRemarks("Remarcks for..");
				product.setType("SS");
				products.add(product);
				count++;
			}
			for (int i = 0; i < 4; i++) {
				ProductDataWrapper product = new ProductDataWrapper();
				product.setId(count);
				product.setItem("Post");
				product.setLength(22);
				product.setWidth(28);
				product.setThick(1200.0);
				product.setQty(12);
				product.setWeight(87);
				product.setArea(221.21);
				product.setFinish("New Finish");
				product.setRemarks("Remarcks for..");
				product.setType("SS");
				products.add(product);
				count++;
			}

			List<HardwareItem> list = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				HardwareItem item = new HardwareItem();
				item.setItem("Hardware Item Name");
				item.setQty(i + 6);
				list.add(item);
			}

			Map<String, Object> parameters = new HashMap<>();

			System.out.println(products);
			JasperReport jasperReport = JasperCompileManager.compileReport(getReportFile("Order_BreakDown.jrxml"));
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(products);
			JRBeanCollectionDataSource dataList = new JRBeanCollectionDataSource(list);
			parameters.put("list", dataList);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
			File file = new File("C:\\Users\\sachint\\Desktop\\temp-file\\New_File.xlsx");

			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setSheetNames(new String[] { "Area & Weight MS" });

			FileOutputStream outputStream = new FileOutputStream(file);
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			outputStream.close();
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getReportFile(String fileName) throws IOException {
		return applicationContext.getResource("classpath:/" + fileName).getInputStream();
	}

}
