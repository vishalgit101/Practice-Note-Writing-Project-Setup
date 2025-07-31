package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	//DI
	private JavaMailSender mailSender;
	
	@Autowired
	public EmailService(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}
	
	public void sendPasswordResetEmail(String to, String resetUrl) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Password Reset Request");
		message.setText("Click the link to reset the password: " + resetUrl);
		this.mailSender.send(message);
	}
	
}	
