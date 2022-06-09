package com.protosyte.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.protosyte.demo.exception.SpringRedditException;
import com.protosyte.demo.model.NotificationEmail;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j //logger
public class MailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private MailContentBuilder mailContentBuilder;
	
	public void sendMail(NotificationEmail notificationEmail) {
		MimeMessagePreparator messagePreparator = mimeMessage ->{
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
			messageHelper.setFrom("springreddit@email.com");
			messageHelper.setTo(notificationEmail.getRecipient());
			messageHelper.setSubject(notificationEmail.getSubject());
			messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
		};
		try {
			mailSender.send(messagePreparator);
			log.info("Activation email sent!!");
		}catch(MailException e) {
			throw new SpringRedditException("exception occured while sending email");
		}
	}
}
