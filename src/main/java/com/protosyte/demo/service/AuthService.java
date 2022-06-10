package com.protosyte.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.protosyte.demo.dto.RegisterRequest;
import com.protosyte.demo.exception.SpringRedditException;
import com.protosyte.demo.model.NotificationEmail;
import com.protosyte.demo.model.User;
import com.protosyte.demo.model.VerificationToken;
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
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEmail(registerRequest.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		userRepository.save(user);
		String token = generateVerificationToken(user);
		mailService.sendMail(new NotificationEmail("Please activare your account, ",user.getEmail(),"Thank you for signing up to RedditProtosyte."+ "\nPlease click on the link below to activate your account: "+ "http://localhost:8080/api/auth/accountVerification/" + token));
		
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
}
