package filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.JwtService;
import services.MyUserDetailsService;

@Component
public class JwtFilter extends OncePerRequestFilter {

	//DI JwtService
	private final JwtService jwtService;
	private ApplicationContext context;
	@Autowired
	public JwtFilter(JwtService jwtService, ApplicationContext context) {
		super();
		this.jwtService = jwtService;
		this.context = context;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//Check for Auth Header
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		if(authHeader !=null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = this.jwtService.extractUsername(token);
			
			// check if the user is not null and if the user if=s not already authenticated
			if(username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
				
				UserDetails userDetails = this.context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
				
				// validate token and Username that we are checking should be part of the database
				if(this.jwtService.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					
					// authToken Should know about requestObject as well
					authToken .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				
			}
			
		}
		
		filterChain.doFilter(request, response);
	}

}
