package com.protosyte.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.protosyte.demo.dto.LoginRequest;
import com.protosyte.demo.dto.RegisterRequest;
import com.protosyte.demo.dto.SessionLoginRequest;
import com.protosyte.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
		authService.signup(registerRequest);
		return new ResponseEntity<>("User registration ok", HttpStatus.OK);
	}
	
	@GetMapping("accountVerification/{token}")
	public ResponseEntity<String> verifyAccount(@PathVariable String token){
		authService.verifyAccount(token);
		return new ResponseEntity<>("Account successfully activated", HttpStatus.OK);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		authService.login(loginRequest);
		return new ResponseEntity<>("User logged in as "+loginRequest.getUsername(), HttpStatus.OK);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestBody SessionLoginRequest sessionLoginRequest) {
		authService.logout(sessionLoginRequest);
		return new ResponseEntity<>("User "+sessionLoginRequest.getUsername()+" logged out", HttpStatus.OK);
	}
}
