package services;

import java.util.List;

import entity.Role;
import entity.Users;

public interface UserService {
	
	Users registerUser(Users user);
	
	Users findByUsername(String username);
	
	Users updateRole(Long userId, String roleName );
	
	Users findByUserId(Long userId);
	
	List<Users> getAll();

	String verify(Users user);

	void updateAccountLockStatus(Long userId, boolean lock);

	List<Role> getAllRoles();

	void updateAccountExpiryStatus(Long userId, boolean lock);

	void updateAccountEnabledStatus(Long userId, boolean lock);

	void updateCredentialsExpiryStatus(Long userId, boolean expire);

	void updatePassword(Long userId, String password);

	void generatePasswordResetToken(String email);

	void resetPassword(String token, String newPassword);
	
}
