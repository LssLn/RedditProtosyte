package com.protosyte.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //for AuthService mail
public class NotificationEmail {
	private String subject;
    private String recipient;
    private String body;
}
