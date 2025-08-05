package restControllers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import DTO.RoleDto;
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
	public Users updateRole(@RequestParam Long userId, @RequestParam String roleName) {
		System.out.println(roleName + "Roles in update role");
		return this.userServiceImpl.updateRole(userId, roleName);
		
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<Users> userById(@PathVariable Long userId) {
		System.out.println("User controller for fetching single user got hit");
		Users user = this.userServiceImpl.findByUserId(userId);
		System.out.println(user);
		return ResponseEntity.ok().body(user);
	}
	
	@PutMapping("/update-lock-status")
	public ResponseEntity<String> updateAccountLockStatus(@RequestParam Long userId, @RequestParam boolean lock){
		this.userServiceImpl.updateAccountLockStatus(userId, lock);
		return ResponseEntity.ok("Account lock status updated");
	}
	
	@GetMapping("/roles")
	public ResponseEntity<List<RoleDto>> getAllRoles(){
		List<Role> roles =  this.userServiceImpl.getAllRoles();
		System.out.println("Roles: " + roles);
		List<RoleDto> rolesDto = new ArrayList<RoleDto>();
		
		for(Role role: roles) {
			rolesDto.add(new RoleDto(role.getId(), "ROLE_" + role.getRole()));
		}
		
		
		
		return ResponseEntity.ok().body(rolesDto);
		
	}
	
	@PutMapping("/update-expiry-status")
	public ResponseEntity<String> updateAccountExpiryStatus(@RequestParam Long userId, @RequestParam boolean expire){
		this.userServiceImpl.updateAccountExpiryStatus(userId, expire);
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
