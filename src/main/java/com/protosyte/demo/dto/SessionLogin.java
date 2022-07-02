package com.protosyte.demo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SessionLogin {
	private String sessionLoginId;
	private String username;
	private Date sessionLoginDate;
}
