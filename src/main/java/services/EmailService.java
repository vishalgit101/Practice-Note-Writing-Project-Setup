package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	//DI
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.default.mail.id}")
	private String defaultMail;
	
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
	
	public void sendContactMessage(String email, String name, String msg) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(this.defaultMail);
		message.setSubject("Contact Form from User: " + email + ", with Name: " + name);
		message.setText(msg);
		this.mailSender.send(message);
	}
	
}	
