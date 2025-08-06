package restControllers;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.web.multipart.MultipartFile;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import entity.Role;
import entity.Users;
import model.UserPrincipal;
import repos.UserRepo;
import services.JwtService;
import services.TotpService;
import services.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	// Di- user repo 
	private final UserRepo userRepo;
	private final UserService userService;
	private final TotpService totpService;
	private final JwtService jwtService;

	@Autowired
	public UserController(UserRepo userRepo, UserService userService, TotpService totpService, JwtService jwtService) {
		super();
		this.userRepo = userRepo;
		this.userService = userService;
		this.totpService = totpService;
		this.jwtService = jwtService;
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
		payload.put("profilePicture", user.getProfilePicture());
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
	public ResponseEntity<String> updateAccountExpiryStatus( @RequestParam boolean expire, @AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.updateAccountExpiryStatus(user.getId(), expire);
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
	
	// For Enabling 2fa
	@PostMapping("/enable-2fa") // this is for generating the qr code and will be enabled after verification
	public ResponseEntity<Map<String, String>> enable2FA(@AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		GoogleAuthenticatorKey secret = this.userService.generateAuthenticatorKey(user.getId());
		String qrCodeUrl = this.totpService.getQrCodeUrl(secret, username);
		Map<String, String> payload = new HashMap<>();
		payload.put("qrCodeUrl", qrCodeUrl);
		return ResponseEntity.ok(payload);
		
	}
	
	@PostMapping("/disable-2fa")
	public ResponseEntity<String> disable2FA(@AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		this.userService.disable2FA(user.getId());
		return ResponseEntity.ok("2FA Disabled Successfully");
		
	}
	
	@PostMapping("/verify-2fa")
	public ResponseEntity<String> verify2FA(@RequestParam int code ,@AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		
		boolean isValid = this.userService.validate2FACode(user.getId(), code);
		
		if(isValid) {
			this.userService.enable2FA(user.getId());
			return ResponseEntity.ok("2FA Disabled Successfully"); 
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA Code");
		}
	}
	
	
	
	
	// For checking if the 2FA is enabled or not
	@GetMapping("/user/2fa-status")
	public ResponseEntity<?> get2FAStatus(@AuthenticationPrincipal UserPrincipal principal) throws UserPrincipalNotFoundException{
		String username = principal.getUsername();
		Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		
		if(user != null) {
			return ResponseEntity.ok().body(Map.of("is2faEnabled", user.isTwoFactorEnabled()));
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not found");
		}
		
	}
	
	// For Login with 2FA
	// and its marked as public cos authentication is not yet successful
	 @PostMapping("/public/verify-2fa-login")
	 public ResponseEntity<String> verify2FALogin(@RequestParam int code, @RequestParam String jwtToken) throws UserPrincipalNotFoundException {
        String username = this.jwtService.extractUsername(jwtToken);
        Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
		
        boolean isValid = userService.validate2FACode(user.getId(), code);
        
        if (isValid) {
            return ResponseEntity.ok("2FA Verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
  	}
	 
	 @PostMapping("/upload/profile-pic")
	 public ResponseEntity<?> uploadProfilePic(@RequestParam MultipartFile file, @AuthenticationPrincipal UserPrincipal principal) throws IOException{
		 
		 System.out.println("upload mapping got hit");
		// ✅ Max size in bytes (2MB)
	    long maxSize = 2 * 1024 * 1024;

	    if (file.getSize() > maxSize) {
	        return ResponseEntity
	                .badRequest()
	                .body("File size exceeds 2MB limit.");
	    }
		 
		 String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
		// Path path = Paths.get("/uploads/profile/" + filename);
		 Path path = Paths.get("D:/uploads/profile", filename);

		 Files.createDirectories(path.getParent());
		 Files.write(path, file.getBytes());
		 
		 String username = principal.getUsername();
		 Users user = this.userRepo.findByUsername(username).orElseThrow(()-> new UserPrincipalNotFoundException("User not found Exception"));
			
		 user.setProfilePicture("/profile/" + filename);
		 this.userRepo.save(user);
		 Map<String, String> payload = new HashMap<String, String>();
		 payload.put("profilePicture", user.getProfilePicture());
		 return ResponseEntity.ok(payload);
	 }
	 
	/* @PostMapping("/upload/profile-pic")
	 public ResponseEntity<String> uploadProfilePic( @AuthenticationPrincipal UserPrincipal principal) {
		 
		 System.out.println("upload mapping got hit");
		return ResponseEntity.ok("Fuck");
	 }*/
	 
	/* @GetMapping("/profile/{filename:.+}")
	 public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) throws IOException {
	     Path imagePath = Paths.get(System.getProperty("user.dir"), "uploads", "profile", filename);
	     if (!Files.exists(imagePath)) {
	         return ResponseEntity.notFound().build();
	     }

	     Resource resource = new UrlResource(imagePath.toUri());
	     String contentType = Files.probeContentType(imagePath);

	     return ResponseEntity.ok()
	             .contentType(MediaType.parseMediaType(contentType))
	             .body(resource);
	 }*/

}