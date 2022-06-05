package com.protosyte.demo.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.protosyte.demo.dto.RegisterRequest;
import com.protosyte.demo.model.User;

@Service
public class AuthService {
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(registerRequest.getPassword());
		user.setEmail(registerRequest.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(false);
	}
}
