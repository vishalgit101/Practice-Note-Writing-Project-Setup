package restControllers;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Users;
import services.UserServiceImpl;

@RestController
@RequestMapping("/api/auth/public/signup") // could change it to /api/auth
public class SignupController {
	
	// DI User Service
	private final UserServiceImpl userServiceImpl;

	public SignupController(UserServiceImpl userServiceImpl) {
		super();
		this.userServiceImpl = userServiceImpl;
	}
	
	@PostMapping // (/public/signup
	public Users registerUser(@RequestBody Users user ) {
		return this.userServiceImpl.registerUser(user);
	}
	
}
