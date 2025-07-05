package restControllers;


import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.Users;
import model.UserPrincipal;
import services.UserServiceImpl;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	// DI 
	private final UserServiceImpl userServiceImpl;

	public AdminController(UserServiceImpl userServiceImpl) {
		super();
		this.userServiceImpl = userServiceImpl;
	}
	
	@GetMapping("/getusers")
	public List<Users> getUsers(@AuthenticationPrincipal UserPrincipal principal) {
		System.out.println("Principal: " + principal.getUsername());
		System.out.println("Get users api method hit");
		return this.userServiceImpl.getAll();
	}
	
	// Update the user role
	@PutMapping("/update-role")
	public Users updateRole(@RequestParam Long userId, @RequestParam String role) {
		
		return this.userServiceImpl.updateRole(userId, role);
		
	}
	
	@GetMapping("/user/{id}")
	public Users userById(@PathVariable Long userId) {
		return this.userServiceImpl.findByUserId(userId);
	}
	
	
}
