package configs;

import java.util.*;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import entity.Role;
import entity.Users;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repos.RoleRepo;
import services.JwtService;
import services.UserService;

// 7 Step Process
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	// DI
	private final UserService userService;
	private final JwtService jwtService;
	private final RoleRepo roleRepo;
	
	// frontendUrl
	@Value("${frontend.url}")
	private String frontendUrl;
	
	private String username; // email in my case
	private String email;
	private String idAttributeKey;
	
	@Autowired
	public OAuth2LoginSuccessHandler(UserService userService, JwtService jwtService, RoleRepo roleRepo) {
		super();
		this.userService = userService;
		this.jwtService = jwtService;
		this.roleRepo = roleRepo;
	}

	// Step 1: define fields and get the AuthToken by down casting it
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
		String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
		
		// Step 2: Check registration id belongs to which OAuth Server with the help of AuthToken ClientRegistationId object
		if("github".equals(registrationId) || "google".equals(registrationId)) {
			
			// getting default oauth principal from the params authentication object that came back with the request
			
			DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal(); // down cast it
			
			//Step 3: Extract Attributes from principal
			Map<String, Object> attributes = principal.getAttributes();
			
			String email = (String) attributes.get("email");
			String name = (String) attributes.get("name");
			
			// Step 4: Handle Different Attributes
			// either we down cast or do toString, same thing here
			if("github".equals(registrationId)) {
				this.username = (String) attributes.getOrDefault("login", ""); // safely checks key exits and returns null bydefault if key not exits which we can modify to add another object, like we added ""
				this.idAttributeKey =  "id";
			}else if("google".equals(registrationId)) {
				this.username = attributes.getOrDefault("email", null).toString();
				this.idAttributeKey = "sub";
			}else {
				 username = "";
	             idAttributeKey = "id";
			}
			
			System.out.println("HELLO OAUTH: " + email + " : " + name + " : " + username);
			
			// Step 5: if user already in dB:- update security context
			Optional<Users> optUser = this.userService.findByUsernameOptional(email);
			
			if(optUser.isPresent()) {
				// get the user
				Users user = optUser.get();
				
				System.out.println("Hello2: " + "isPresent");
				
				List<SimpleGrantedAuthority> authorities = new ArrayList<>();
				Set<Role> tempRoles = user.getRoles();
				
				for( Role tempRole : tempRoles) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" + tempRole.getRole()));
				}
				
				DefaultOAuth2User oauthUser = new DefaultOAuth2User(authorities, attributes, this.idAttributeKey);
				
				Authentication securityAuth = new OAuth2AuthenticationToken(oauthUser, authorities, registrationId);
				
				SecurityContextHolder.getContext().setAuthentication(securityAuth);
				
			}else { //Step 6: if user wasn't in dB:- add the user in dB and update the security context
				// create the new user
				Users newUser = new Users();
				Optional<Role>  role = this.roleRepo.findByRole("USER");
				
				System.out.println("Hello3: " + "isNotPresent");
				
				/*if(role.isPresent()) {
					System.out.println("Hello4");
					newUser.addRole(role.get()); // Set existing role... .get() cos its optional
					System.out.println("Hello5");
				}else {
					throw new RuntimeException("Default role not found");
				}*/
				System.out.println("username in ouathsuccesshandle: " + username);
				newUser.setUsername(email);
				newUser.setSignUpMethod(registrationId);
				newUser.setPassword(""); // this is very important to get handle well with backend check so that no one can enter a blank password and able to access user account see gpt discussion
				this.userService.registerUser(newUser);
				System.out.println();
				// Repeat Step 5 -- can also just create a method to avoid repetition here
				
				List<SimpleGrantedAuthority> authorities = new ArrayList<>();
				Set<Role> tempRoles = newUser.getRoles();
				
				for( Role tempRole : tempRoles) {
					authorities.add(new SimpleGrantedAuthority("ROLE_" + tempRole.getRole()));
				}
				
				DefaultOAuth2User oauthUser = new DefaultOAuth2User(authorities, attributes, this.idAttributeKey);
				
				Authentication securityAuth = new OAuth2AuthenticationToken(oauthUser, authorities, registrationId);
				
				SecurityContextHolder.getContext().setAuthentication(securityAuth);
				
			}
			
			 this.setAlwaysUseDefaultTargetUrl(true);
			
			/*// Extract OAuth2 user
			DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
			Map<String, Object> attributes = oauth2User.getAttributes();

			// Extract email (only this is needed)
			String email = (String) attributes.get("email");*/

			System.out.println("Hello6 OAuth2LoginSuccessHandler: email = " + email);

			/*// Fetch the user from DB (you might want to register if not found, as discussed before)
			Users user = userService.findByUsernameOptional(email)
			        .orElseThrow(() -> new RuntimeException("User not found"));*/

			// Generate JWT using only email
			String jwtToken = jwtService.generateToken(email, false);  // JWT subject = email, for oauth sign in people no option for 2fa?
			
			 // Redirect to the frontend with the JWT token
	        String targetUrl = UriComponentsBuilder.fromUriString(this.frontendUrl + "/oauth2/redirect")
	                .queryParam("token", jwtToken)
	                .build().toUriString();
	        this.setDefaultTargetUrl(targetUrl);
	        super.onAuthenticationSuccess(request, response, authentication);
	        
	        System.out.println("Success with OAUth");
			
		}
		
	}
		
}

// Try this code someday
/*
 
 // 1. Check if user exists by email
    Optional<Users> optUser = userRepo.findByEmail(email);  // or userService.findOptionalByEmail()

    Users user;
    if (optUser.isPresent()) {
        user = optUser.get();
    } else {
        // 2. Register new user (you can extract this logic into a helper)
        user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setName(name);
        user.setPassword(""); // OAuth doesn't give you a password
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        // Assign default role
        Role defaultRole = roleRepo.findByRole("USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(defaultRole));

        user = userRepo.save(user);
    }

    // 3. Wrap in your UserPrincipal
    UserPrincipal userPrincipal = new UserPrincipal(user);

    // 4. Authenticate manually
    UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    // 5. Redirect (or continue processing)
    response.sendRedirect("/home"); // or dashboard, etc.
}
  
  */


/*Orignal Code
package com.secure.notes.config;

import com.secure.notes.models.AppRole;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.security.jwt.JwtUtils;
import com.secure.notes.security.services.UserDetailsImpl;
import com.secure.notes.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    RoleRepository roleRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    String username;
    String idAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || "google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();
            if ("github".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = attributes.getOrDefault("login", "").toString();
                idAttributeKey = "id";
            } else if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
                username = email.split("@")[0];
                idAttributeKey = "sub";
            } else {
                username = "";
                idAttributeKey = "id";
            }
            System.out.println("HELLO OAUTH: " + email + " : " + name + " : " + username);

            userService.findByEmail(email)
                    .ifPresentOrElse(user -> {
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    }, () -> {
                        User newUser = new User();
                        Optional<Role> userRole = roleRepository.findByRoleName(AppRole.ROLE_USER); // Fetch existing role
                        if (userRole.isPresent()) {
                            newUser.setRole(userRole.get()); // Set existing role
                        } else {
                            // Handle the case where the role is not found
                            throw new RuntimeException("Default role not found");
                        }
                        newUser.setEmail(email);
                        newUser.setUserName(username);
                        newUser.setSignUpMethod(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                        userService.registerUser(newUser);
                        DefaultOAuth2User oauthUser = new DefaultOAuth2User(
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                attributes,
                                idAttributeKey
                        );
                        Authentication securityAuth = new OAuth2AuthenticationToken(
                                oauthUser,
                                List.of(new SimpleGrantedAuthority(newUser.getRole().getRoleName().name())),
                                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                        );
                        SecurityContextHolder.getContext().setAuthentication(securityAuth);
                    });
        }
        this.setAlwaysUseDefaultTargetUrl(true);

        // JWT TOKEN LOGIC
        DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Extract necessary attributes
        String email = (String) attributes.get("email");
        System.out.println("OAuth2LoginSuccessHandler: " + username + " : " + email);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>(oauth2User.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList()));
        User user = userService.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found"));
        authorities.add(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));

        // Create UserDetailsImpl instance
        UserDetailsImpl userDetails = new UserDetailsImpl(
                null,
                username,
                email,
                null,
                false,
                authorities
        );

        // Generate JWT token
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Redirect to the frontend with the JWT token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("token", jwtToken)
                .build().toUriString();
        this.setDefaultTargetUrl(targetUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
 * */
