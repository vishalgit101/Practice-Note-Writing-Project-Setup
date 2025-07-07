package restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Users;
import services.UserService;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	//DI UserService
	private final UserService userService;
	
	@Autowired
	public LoginController(UserService userService) {
		super();
		this.userService = userService;
	}



	@GetMapping
	public String login( @RequestBody Users user) {
		System.out.println("Login controller got hit");
		System.out.println("Users: " + user);
		//return "success";
		return this.userService.verify(user);
	}
	
}
