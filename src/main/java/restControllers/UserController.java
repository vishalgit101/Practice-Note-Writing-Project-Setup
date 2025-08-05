package restControllers;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import entity.Role;
import entity.Users;
import model.UserPrincipal;
import repos.UserRepo;
import services.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	// Di- user repo 
	private final UserRepo userRepo;
	private final UserService userService;

	@Autowired
	public UserController(UserRepo userRepo, UserService userService) {
		super();
		this.userRepo = userRepo;
		this.userService = userService;
	}

	@GetMapping("/user") // not public
	public ResponseEntity<Map<String, Object>> getUser(@AuthenticationPrincipal UserPrincipal principal){
		System.out.println("User Principal in auth user: " + principal.getUsername());
		Optional<Users> tempUser = this.userRepo.findByUsername(principal.getUsername());
		
		Users user = tempUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		Set<Role> roles = user.getRoles();
		
		Map<String, Object> payload = new HashMap<String, Object>();
		
		payload.put("Username", user.getUsername());
		payload.put("Roles", roles );
		payload.put("Id", user.getId());
		payload.put("enabled", user.isEnabled()); // enabled
		payload.put("createdDate", user.getCreatedDate());
		payload.put("updatedDate", user.getUpdatedDate());
		payload.put("accountNonExpired", user.isAccountNonExpired());
		payload.put("accountNonLocked", user.isAccountNonLocked());
		payload.put("credentialsNonExpired", user.isCredentialsNonExpired());
		payload.put("twoFactorEnabled", user.isTwoFactorEnabled());
		return ResponseEntity.status(HttpStatus.OK).body(payload);
		
	}
	
	@PutMapping("/update-lock-status")
	public ResponseEntity<String> updateAccountLockStatus(@RequestParam boolean lock, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.updateAccountLockStatus(user.getId(), lock);
		return ResponseEntity.ok("Account lock status updated");
	}
	
	@PutMapping("/update-expiry-status")
	public ResponseEntity<String> updateAccountExpiryStatus( @RequestParam boolean lock, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.updateAccountExpiryStatus(user.getId(), lock);
		return ResponseEntity.ok("Account Expiry Status Updated");
	}
	
	@PutMapping("/update-enabled-status")
	public ResponseEntity<String> updateAccountEnabledStatus( @RequestParam boolean enabled, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.updateAccountEnabledStatus(user.getId(), enabled);
		return ResponseEntity.ok("Account Enabled Status Updated");
	}
	
	@PutMapping("/update-credentials-expiry-status")
	public ResponseEntity<String> updateCredentialsExpiryStatus(@RequestParam boolean expire, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.updateCredentialsExpiryStatus(user.getId(),expire);
		return ResponseEntity.ok("Credentials Expiry Status Updated");
	}
	
	@PostMapping("/update-credentials")
	public ResponseEntity<String> updatePassword(@RequestParam String newUsername, @RequestParam String newPassword, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		
		try {
			this.userService.updateCredentials(user, newUsername, newPassword);
			return ResponseEntity.ok("Account credentails has been updated");
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}	
	}
}