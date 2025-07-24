package restControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import services.JwtService;

@RestController
@RequestMapping("/api")
public class CsrfController {
	// DI
	private final JwtService jwt;
	
	@Autowired
	public CsrfController(JwtService jwt) {
		super();
		this.jwt = jwt;
	}

	@GetMapping("/csrf-token")
	public CsrfToken csrfToken(HttpServletRequest request) {
		return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
	}
	
	@PostMapping("/admin/secret-key")
	public String secretKey() {
		System.out.println("Secret key func hit");
		return this.jwt.generateKey();
	}
}
