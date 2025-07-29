package restControllers;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.Role;
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
	
	@PutMapping("/update-lock-status")
	public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId, @RequestParam boolean lock){
		this.userServiceImpl.updateAccountLockStatus(userId, lock);
		return ResponseEntity.ok("Account lock status updated");
	}
	
	@GetMapping("/roles")
	public List<Role> getAllRoles(){
		return this.userServiceImpl.getAllRoles();
	}
	
	@PutMapping("/update-expiry-status")
	public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId, @RequestParam boolean lock){
		this.userServiceImpl.updateAccountExpiryStatus(userId, lock);
		return ResponseEntity.ok("Account Expiry Status Updated");
	}
	
	@PutMapping("/update-enabled-status")
	public ResponseEntity<String> updateAccountEnabledStatus(@RequestParam Long userId, @RequestParam boolean enabled){
		this.userServiceImpl.updateAccountEnabledStatus(userId, enabled);
		return ResponseEntity.ok("Account Enabled Status Updated");
	}
	
	@PutMapping("/update-credentials-expiry-status")
	public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire){
		this.userServiceImpl.updateCredentialsExpiryStatus(userId,expire);
		return ResponseEntity.ok("Credentials Expiry Status Updated");
	}
	
	@PutMapping("/update-password")
	public ResponseEntity<String> updatePassword(@RequestParam Long userId, @RequestParam String password){
		try {
			this.userServiceImpl.updatePassword(userId, password);
			return ResponseEntity.ok("Account password has been updated");
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

	}
	
}
