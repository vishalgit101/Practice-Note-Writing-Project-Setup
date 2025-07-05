package services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import entity.Users;
import model.UserPrincipal;
import repos.UserRepo;

@Service
public class MyUserDetailsService implements  UserDetailsService{

	private final UserRepo userRepo;
	
	public MyUserDetailsService(UserRepo userRepo) {
		super();
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// loading the user from the dB
		Optional<Users> optionalUser = this.userRepo.findByUsername(username);
		
		Users user = optionalUser.orElseThrow(()-> new UsernameNotFoundException("User not Found with user name and password"));
		
		/*if(user == null) {
			throw new RuntimeException("User not found!");
		}*/
		return new UserPrincipal(user);
	}

}
