package restControllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import services.UserService;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
	// DI password reset service
	private final UserService userService;
	
	public PasswordResetController(UserService userService) {
		super();
		this.userService = userService;
	}

	@PostMapping("/public/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String email){
		try {
			this.userService.generatePasswordResetToken(email);
			return ResponseEntity.ok().body("Password Reset Email Sent");
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending the password rest email");
		}
	}
	
	@PostMapping("/public/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword){
		try {
			this.userService.resetPassword(token, newPassword);
			return ResponseEntity.ok().body("Password Successfully Upadted");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		
	}
}
