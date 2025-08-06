package services;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private String secretKey = "";
	
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;
	
	
	// can also set the expiration time from the applications properties
	
	// for generating a random key
	/*public JwtService() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public String generateToken(String username, boolean is2faEnabled) {
		
		// My Own code just includes the username in the jwt token
		// but roles can be added depending on the frontend or your app architecture
		
		System.out.println("Generate Token Hit");
		
		Map<String, Object> claimsMap = new HashMap<String, Object>();
		claimsMap.put("is2faEnabled", is2faEnabled); // is2faEnabled
		
		return Jwts.builder()
			//.claims()
			//.add(claimsMap)
			.claim("is2faEnabled", is2faEnabled)
			.subject(username)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 8)) // expire in 8h, use L, to avoid integer overflow
			//.and()
			.signWith(getKey())
			.compact();
		
		
		
	}

	/*private Key getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes); // convert into byte before passing secret key inside hmacShaKeyFor
	}*/
	
	private Key getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes); // convert into byte before passing secret key inside hmacShaKeyFor
	}
	
	//generate key
	public String generateKey() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			this.secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.secretKey;
	}

	public Claims extractAllClaims(String token) {
	    return Jwts
	        .parser()
	        .verifyWith((SecretKey) getKey())
	        .build()
	        .parseSignedClaims(token) // JJWT 0.12.x
	        .getPayload();            // Get claims body
	}


	public String extractUsername(String token) {
	    return extractAllClaims(token).getSubject();
	}

	
	public boolean validateToken(String token, UserDetails userDetails) {
	    final String username = extractUsername(token);
	    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	
	public Date extractExpiration(String token) {
	    return extractAllClaims(token).getExpiration();
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = extractAllClaims(token);
	    return claimsResolver.apply(claims);
	}


	
	public boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}

	
}
