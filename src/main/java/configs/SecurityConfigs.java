package configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfigs {
	
	// DI Service and jwt filters etc
	private UserDetailsService myUserDetailsService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
					.httpBasic(Customizer.withDefaults())
					.build();
					
	}
	
	@Bean
	public UserDetailsService userDetails() {
		UserDetails user1 = User.builder().username("Vishal").password("{noop}pass123").roles("admin", "manager", "user").build();
		
		UserDetails user2 = User.builder().username("Harjeet").password("{noop}pass123").roles("manager", "user").build();
		
		UserDetails user3 = User.builder().username("NoOne").password("{noop}pass123").roles("user").build();
		
		return new InMemoryUserDetailsManager(user1, user2, user3);
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
		// set userservice
		provider.setUserDetailsService(null); // replace null with the service
		return provider;
	}
	
}
