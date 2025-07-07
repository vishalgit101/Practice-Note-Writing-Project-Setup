package services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import entity.Role;
import entity.Users;
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
	
	@Autowired
	public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo, AuthenticationManager authManager, JwtService jwtService) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.authManager = authManager;
		this.jwtService = jwtService;
	}

	@Override
	public Users registerUser(Users user) {
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
	
	

}
