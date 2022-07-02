package com.protosyte.demo.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.core.io.ByteArrayResource;

import lombok.Data;

@Data
@Entity
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	@NotBlank(message="username is required")
	private String username;
	@NotEmpty(message="password required")
	private String password;
	
	@Email
	@NotEmpty(message="Email is required")
	private String email;
	private Instant created;
	private boolean enabled;
	private ByteArrayResource salt;
}
