package model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

import entity.Role;
import entity.Users;

public class UserPrincipal implements UserDetails{

	private Users user;
	
	public UserPrincipal(Users user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		/*List<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
		roles.add(new SimpleGrantedAuthority("user"));*/
		
		// get the roles form the dB and user
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		
		Set<Role> tempRoles = this.user.getRoles();
		
		for( Role tempRole : tempRoles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + tempRole.getRole()));
		}
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

}
