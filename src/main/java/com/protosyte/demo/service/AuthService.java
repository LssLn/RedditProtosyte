package com.protosyte.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.protosyte.demo.dto.LoginRequest;
import com.protosyte.demo.dto.RegisterRequest;
import com.protosyte.demo.dto.SessionLoginRequest;
import com.protosyte.demo.exception.SpringRedditException;
import com.protosyte.demo.model.NotificationEmail;
import com.protosyte.demo.model.SessionLogin;
import com.protosyte.demo.model.User;
import com.protosyte.demo.model.VerificationToken;
import com.protosyte.demo.repository.SessionRepository;
import com.protosyte.demo.repository.UserRepository;
import com.protosyte.demo.repository.VerificationTokenRepository;

@Service
public class AuthService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	VerificationTokenRepository verificationTokenRepository;
	@Autowired 
	PasswordEncoder passwordEncoder;
	@Autowired
	private MailService mailService;
	@Autowired
	SessionRepository sessionRepository;
	@Autowired
	private EntityManager entityManager;

	
	private static Logger logger = LoggerFactory.getLogger(AuthService.class);
	
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
//		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		try {
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			logger.info(" -- salt: {}",salt);

//			ByteArrayResource saltBAR = new ByteArrayResource(salt);
//			logger.info(" -- saltStr: {}",saltBAR);
//			logger.info(" -- saltStr.toString: {}",saltBAR.toString());
			
			byte[] hashedPw  = passwordEncryption(registerRequest.getPassword(), salt);
			String hashedPwStr = new String(hashedPw);
			logger.info(" -- hashedPW: {}",hashedPw);
			logger.info(" -- hashedPWStr: {}",hashedPwStr);
			
			logger.info("hashcode: {}",hashedPw.hashCode());
			
			String hashedPwStrUtf8 = new String(new byte[]{ (byte)0x63 }, StandardCharsets.UTF_8);
			logger.info(" -- hashedPwStrUtf8: {}",hashedPwStrUtf8);
			String hashedPwStrISO_8859_1 = new String(new byte[]{ (byte)0x63 }, StandardCharsets.ISO_8859_1);
			logger.info(" -- hashedPwStrISO_8859_1: {}",hashedPwStrISO_8859_1);
			String hashedPwStrUS_ASCII = new String(new byte[]{ (byte)0x63 }, StandardCharsets.US_ASCII);
			logger.info(" -- hashedPwStrUS_ASCII: {}",hashedPwStrUS_ASCII);
			String hashedPwStrUTF_16 = new String(new byte[]{ (byte)0x63 }, StandardCharsets.UTF_16);
			logger.info(" -- hashedPwStrUTF_16: {}",hashedPwStrUTF_16);
			String hashedPwStrUTF_16BE = new String(new byte[]{ (byte)0x63 }, StandardCharsets.UTF_16BE);
			logger.info(" -- hashedPwStrUTF_16BE: {}",hashedPwStrUTF_16BE);
			String hashedPwStrUTF_16LE = new String(new byte[]{ (byte)0x63 }, StandardCharsets.UTF_16LE);
			logger.info(" -- hashedPwStrUTF_16LE: {}",hashedPwStrUTF_16LE);
			
			user.setPassword(hashedPwStr);
			user.setEmail(registerRequest.getEmail());
			user.setCreated(Instant.now());
			user.setEnabled(false);
			user.setSalt(new ByteArrayResource(salt));
			
			logger.info("saltStr: {}",hashedPwStr);
			
			userRepository.save(user);
			String token = generateVerificationToken(user);
			mailService.sendMail(new NotificationEmail("Please activare your account, ",user.getEmail(),"Thank you for signing up to RedditProtosyte."+ "\nPlease click on the link below to activate your account: "+ "http://localhost:8080/api/auth/accountVerification/" + token));
			
		}catch(NoSuchAlgorithmException e) {
			logger.error("NoSuchAlgorithmException during encryption: {}",e);
		}
		
	}
	
	private byte[] passwordEncryption(String password, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		
		byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
//		logger.info(" -- hashedPW: {}",hashedPassword);

		return hashedPassword;
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		
		verificationTokenRepository.save(verificationToken);
		return token;
	}
	
	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
//		verificationToken.orElseThrow(()-> new SpringRedditException("Invalid token"));
		if(verificationToken.get() != null) {
			fetchUserAndEnable(verificationToken.get());
		}else {
			throw new SpringRedditException("Invalid token");
		}
	}
	
	@Transactional
	public void fetchUserAndEnable(VerificationToken token) {
		String username = token.getUser().getUsername();
		Optional<User> user = userRepository.findByUsername(username);
		if(user.get() != null) {
			user.get().setEnabled(true);
			userRepository.save(user.get());
		}else {
			throw new SpringRedditException("Username associated to token not found");
			
		}
	}

	@Transactional
	public void login(LoginRequest loginRequest) {
		//check login data == users table data. If login is successful, save a session object into session table
		
		ByteArrayResource saltABR = queryFindSaltByUsername(loginRequest.getUsername());
		if(saltABR.exists()) {
			byte[] salt = saltABR.getByteArray();
			try {
				Boolean validLogin = queryLogin(loginRequest,salt);
				//every page that requires authentication needs the session object passed too, along which everything else
				//i.e.: /search will require the session (see logout), the string you are searching for, and the others params.
				//i.e.: /home will require the session (see logout) and nothing else.	
				//If the session is not valid (checks session.getUsername,sessionLoginId == users table & session table)
				if(validLogin) {
					SessionLogin sessionLogin = new SessionLogin();
					
					SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
					Date dateNow = new Date(System.currentTimeMillis());
					try {
						String sessionId = loginRequest.getUsername() + passwordEncryption(loginRequest.getPassword(),salt) + dateNow;
						logger.info("session login for user {}: {}",loginRequest.getUsername()+sessionId);
						
						logger.info("currentMs is "+dateNow+"while instant now returns: ",Instant.now());
						
						sessionLogin.setSessionLoginId(sessionId);
						sessionLogin.setUsername(loginRequest.getUsername());
//						session.setSessionLoginDate(dateNow);
						
						sessionRepository.save(sessionLogin);
					}catch(Exception e) {
						logger.error("Error encrypting password");
					}
					
				}else {
					logger.info("Login failed login not valid");
					throw new SpringRedditException("Login failed.\nWrong username or password");
					
				}
			}catch(Exception e) {
				logger.error("Error encrypting no such algorithm");
			}
			
		}else {
			logger.info("Login failed salt is null");
			throw new SpringRedditException("Login failed.\nNo such user exists");
			
		}
	}
	
	private ByteArrayResource queryFindSaltByUsername(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT user.salt FROM user "
				+ "WHERE user.username='%s' "
				,username));
		try {
			logger.info("findSaltQuery: {}",sb.toString());
			Query query = entityManager.createNativeQuery(sb.toString(),ByteArrayResource.class);
			ByteArrayResource result = (ByteArrayResource)query.getSingleResult();
			logger.info("salt returned: {}",result);
			return result;
		}catch(Exception e) {
			logger.error("SQL Exception attempting salt retrieval");
		}
		return null;
	}

	private Boolean queryLogin(LoginRequest loginRequest, byte[] salt) throws NoSuchAlgorithmException {
		Boolean successfulLogin = false;
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT count(*) FROM user "
				+ "WHERE user.username='%s' "
				+ "AND user.password='%s'", loginRequest.getUsername(), passwordEncryption(loginRequest.getPassword(),salt)));
		try {
			logger.info("LoginQuery: {}",sb.toString());
			Query query = entityManager.createNativeQuery(sb.toString(),Integer.class);
			Integer result = (Integer)query.getSingleResult();
			logger.info("Login returned: {}",result);
			if(result>0) {
				successfulLogin=true;
				return successfulLogin;
			}
		}catch(Exception e) {
			logger.error("SQL Exception attempting login");
		}
		return successfulLogin;
	}

	@Transactional
	public void logout(SessionLoginRequest sessionLoginRequest) {
		//destroys sessionLogin
		//SQL delete from session table where sessionLoginId = %s, sessionLogin.getSessionLoginId()
		
		//todo
	}
}
