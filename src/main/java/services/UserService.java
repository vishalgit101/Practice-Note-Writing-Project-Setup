package services;

import java.util.List;

import entity.Users;

public interface UserService {
	
	Users registerUser(Users user);
	
	Users findByUsername(String username);
	
	Users updateRole(Long userId, String roleName );
	
	Users findByUserId(Long userId);
	
	List<Users> getAll();

	String verify(Users user);
	
}
