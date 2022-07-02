package com.protosyte.demo.model;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
@Entity
public class SessionLogin {
	@Id
//	@GeneratedValue(strategy=GenerationType.)
	private String sessionLoginId;
	@NotBlank
	private String username;
	
	private Date sessionLoginDate;

}
