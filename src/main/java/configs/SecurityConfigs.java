package configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import filter.JwtFilter;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class SecurityConfigs {
	
	// DI Service and jwt filters etc
	private UserDetailsService myUserDetailsService;
	
	private JwtFilter jwtFilter;
	
	private OAuth2LoginSuccessHandler successHandler;
	
	@Autowired
	@Lazy
	public SecurityConfigs(UserDetailsService myUserDetailsService, JwtFilter jwtFilter, OAuth2LoginSuccessHandler successHandler) {
		super();
		this.myUserDetailsService = myUserDetailsService;
		this.jwtFilter = jwtFilter;
		this.successHandler = successHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
					.cors(Customizer.withDefaults())
					/*.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
							.ignoringRequestMatchers("/api/auth/public/**", "/login", "/signup")
							)*/
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> 
						auth.requestMatchers("/login", "/signup").permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/csrf-token").permitAll()
						.requestMatchers("/api/auth/public/**").permitAll()
						.requestMatchers("/oauth2/**").permitAll()
						.requestMatchers("/profile/**").permitAll()
						.anyRequest().authenticated())
					
					.exceptionHandling(exception -> exception
						    .authenticationEntryPoint((request, response, authException) -> {
						        // Handles unauthenticated access (401)
						        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						    })
						    .accessDeniedHandler((request, response, accessDeniedException) -> {
						        // Handles forbidden access or CSRF failures (403)
						        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						        response.setContentType("application/json");
						        response.getWriter().write("{\"error\": \"CSRF token missing or invalid\"}");
						    })
						)

					
					.oauth2Login(oauth2 -> {
						oauth2.successHandler(this.successHandler);
					})
					
					.formLogin(Customizer.withDefaults())
					//.httpBasic(basic -> basic.disable())
					//.formLogin(form -> form.disable())
					.httpBasic(Customizer.withDefaults())
					.sessionManagement(session  -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
					
	}
	
	/*@Bean
	public UserDetailsService userDetails() {
		UserDetails user1 = User.builder().username("Vishal").password("{noop}pass123").roles("admin", "manager", "user").build();
		
		UserDetails user2 = User.builder().username("Harjeet").password("{noop}pass123").roles("manager", "user").build();
		
		UserDetails user3 = User.builder().username("NoOne").password("{noop}pass123").roles("user").build();
		
		return new InMemoryUserDetailsManager(user1, user2, user3);
	}*/
	
	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
		// set userservice
		provider.setUserDetailsService(this.myUserDetailsService); // replace null with the service
		return provider;
	}
	
	//auth manager
	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	} 
	
   /* @Bean
    public GoogleAuthenticator googleAuthenticator() {
        return new GoogleAuthenticator();
    }*/
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	    return (web) -> web.ignoring()
	            .requestMatchers("/profile/**");
	}
	
}
