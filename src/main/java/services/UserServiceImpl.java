package services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import entity.Role;
import entity.Users;
import repos.RoleRepo;
import repos.UserRepo;

public class UserServiceImpl implements UserService {

	// Di Repo
	private final UserRepo userRepo;
	private final RoleRepo roleRepo;
	
	@Autowired
	public UserServiceImpl(UserRepo userRepo, RoleRepo roleRepo) {
		super();
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
	}

	@Override
	public Users registerUser(Users user) {
		// also get the role from the db to assign a default role 
		Optional<Role> optionalRole = this.roleRepo.findByRole("USER");
		Role role = optionalRole.orElseThrow(() -> new RuntimeException("No Role found with role name USER"));
		user.addRole(role);
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
	
	

}
