package com.protosyte.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class MailContentBuilder {
	@Autowired
	private TemplateEngine templateEngine;
	
	String build(String message) {
		Context context = new Context();
		context.setVariable("message", message);
		return templateEngine.process("mailTemplate",context);
	}
}
