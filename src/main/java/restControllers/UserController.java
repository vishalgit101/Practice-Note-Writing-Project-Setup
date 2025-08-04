package restControllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Role;
import entity.Users;
import model.UserPrincipal;
import repos.UserRepo;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	// Di- user repo 
	private final UserRepo userRepo;

	@Autowired
	public UserController(UserRepo userRepo) {
		super();
		this.userRepo = userRepo;
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
		payload.put("createdDate", user.getCreatedDate());
		payload.put("updatedDate", user.getUpdatedDate());
		payload.put("accountNonExpired", user.isAccountNonExpired());
		payload.put("accountNonLocked", user.isAccountNonLocked());
		payload.put("credentialsNonExpired", user.isCredentialsNonExpired());
		payload.put("twoFactorEnabled", user.isTwoFactorEnabled());
		return ResponseEntity.status(HttpStatus.OK).body(payload);
		
	}
	
}
