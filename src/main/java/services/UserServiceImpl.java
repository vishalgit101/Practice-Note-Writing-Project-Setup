package services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import entity.PasswordResetToken;
import entity.Role;
import entity.Users;
import repos.PasswordRestTokenRepo;
import repos.RoleRepo;
import repos.UserRepo;

@Service
public class UserServiceImpl implements UserService {

	// Di Repo
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	private final AuthenticationManager authManager;
	private final JwtService jwtService;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
	
	private final PasswordRestTokenRepo passwordRestTokenRepo;
	private final EmailService emailService;
	
	@Value("${frontend.url}")
	private String frontendUrl;
	
	@Autowired
	public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo, AuthenticationManager authManager, JwtService jwtService, PasswordRestTokenRepo passwordRestTokenRepo, EmailService emailService) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.authManager = authManager;
		this.jwtService = jwtService;
		this.passwordRestTokenRepo = passwordRestTokenRepo;
		this.emailService = emailService;
	}

	@Override
	public Users registerUser(Users user) {
		
		//first check if the user exits already or not
		//Optional<Users> optionalUser = this.userRepo.findByUsername(user.getUsername());
		
		if(this.userRepo.existsByUsername(user.getUsername())) {
			System.out.println("User already exists");
			throw new RuntimeException("User already exists");
		}
		
		// also get the role from the db to assign a default role 
		Optional<Role> optionalRole = this.roleRepo.findByRole("USER");
		Role role = optionalRole.orElseThrow(() -> new RuntimeException("No Role found with role name USER"));
		user.addRole(role);
		user.setPassword(encoder.encode(user.getPassword()));
		user.setCreatedDate(LocalDateTime.now());
		return this.userRepo.save(user);
	}

	@Override // admin ??
	public Users findByUsername(String username) {
		Optional<Users> optionalUser = this.userRepo.findByUsername(username);
		Users user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " +  username));
		
		return user;
	}
	
	@Override
	public Users findByUserId(Long userId) {
		Optional<Users> tempUser =  this.userRepo.findById(userId);
		Users user = tempUser.orElseThrow(()-> new UsernameNotFoundException("User not found with user id: " + userId));
		return user;
	}
	
	@Override
	public List<Users> getAll() { 
		return this.userRepo.findAll();
	}
	
	@Override
	public Users updateRole(Long userId, String roleName ) {
		// find user and then role from the db
		Optional<Users> optionalUser = this.userRepo.findById(userId);
		Users user = optionalUser.orElseThrow(() ->  new UsernameNotFoundException("User not found with user id of: " + userId));
		
		Optional<Role> optionalRole = this.roleRepo.findByRole(roleName);
		Role role = optionalRole.orElseThrow(() -> new RuntimeException("No role with Role Name found: " + roleName));
		
		user.addRole(role);
		//return user; // thats why updating in memeory was needed
		return this.userRepo.save(user);
		
	}

	@Override
	public String verify(Users user) {
		// get the auth-manager, that will use/call the daoprovider for matching/verifying the credentials
		
		// Get the Authentication object and use the authManager object to  authenticate the user
		Authentication authentication = this.authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		
		// Its like Authentication Object (UnAuthenticated) -> Through this.authManager -> Becomes Authenticated Object 
		
		if(authentication.isAuthenticated()) {
			//return "success";
			System.out.println("Is Authenticated Condiational Hit");
			return this.jwtService.generateToken(user.getUsername());
		}
		
		return "fail";
	}

	@Override
	public void updateAccountLockStatus(Long userId, boolean lock) {
		Optional<Users> optionalUser = this.userRepo.findById(userId);
		Users tempUser = optionalUser.orElseThrow(()-> new UsernameNotFoundException("No User Found"));
		tempUser.setAccountNonLocked(!lock);
		this.userRepo.save(tempUser);
	}

	@Override
	public List<Role> getAllRoles() {
		
		List<Role> roles =  this.roleRepo.findAll();
		return roles;
	}
	
	@Override
	public void updateAccountExpiryStatus(Long userId, boolean expire) {
		
		Optional<Users> optionalUser = this.userRepo.findById(userId);
		Users tempUser = optionalUser.orElseThrow(()-> new UsernameNotFoundException("No User Found"));
		tempUser.setAccountNonExpired(!expire);
		this.userRepo.save(tempUser);
		
	}
	
	@Override
	public void updateAccountEnabledStatus(Long userId, boolean enabled) {
		
		Optional<Users> optionalUser = this.userRepo.findById(userId);
		Users tempUser = optionalUser.orElseThrow(()-> new UsernameNotFoundException("No User Found"));
		tempUser.setEnabled(!enabled);
		this.userRepo.save(tempUser);
		
	}

	@Override
	public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
		
		Optional<Users> optionalUser = this.userRepo.findById(userId);
		Users tempUser = optionalUser.orElseThrow(()-> new UsernameNotFoundException("No User Found"));
		tempUser.setCredentialsNonExpired(!expire);
		this.userRepo.save(tempUser);
		
	}

	@Override
	public void updatePassword(Long userId, String password) {
		
		try {
			Optional<Users> optionalUser = this.userRepo.findById(userId);
			Users tempUser = optionalUser.orElseThrow(()-> new UsernameNotFoundException("No User Found"));
			tempUser.setPassword(this.encoder.encode(password));
			this.userRepo.save(tempUser);
		}catch (Exception e) {
			throw new RuntimeException("Failed to Update the password");
		}
		
	}
	
	@Override
	public void generatePasswordResetToken(String email) {
		// find user by that email
		Optional<Users> optUser = this.userRepo.findByUsername(email);
		Users user = optUser.orElseThrow(()-> new UsernameNotFoundException("User Not found"));
		
		// now pass the user in the constructor of password reset token object's instance
		PasswordResetToken passwordResetToken = new PasswordResetToken(user); // its constructor did the job of creating everything
		
		// Now save this instance in its repo
		this.passwordRestTokenRepo.save(passwordResetToken);
		
		String resetUrl = frontendUrl + "/reset-password?token=" + passwordResetToken.getToken();
		
		// Send Email to the User
		this.emailService.sendPasswordResetEmail(user.getUsername(), resetUrl); // username is email
	}

	@Override
	public void resetPassword(String token, String newPassword) {
		
		// check if the token is not expired
		Optional<PasswordResetToken> optResetToken =  this.passwordRestTokenRepo.findByToken(token);
		
		PasswordResetToken resetToken = optResetToken.orElseThrow(()-> new RuntimeException("Token not found"));
		
		if(resetToken.getExpiry().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token Got Expired");
		}
		
		if(resetToken.isUsed()) {
			throw new RuntimeException("Token is Already used");
		}
		
		// fetch the user with the token
		//Optional<Users> optUser = this.userRepo.findById(resetToken.getUser().getId()); // user is lazy loaded?
		
		// resetToken already have the user
		Users user = resetToken.getUser();		
		// then upadate the user password and then change the user password
		user.setPassword(this.encoder.encode(newPassword));
		resetToken.setUsed(true);
		
		this.passwordRestTokenRepo.save(resetToken);
		this.userRepo.save(user);
	}
	
	

}
