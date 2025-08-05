package services;

import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public interface TotpService {

	GoogleAuthenticatorKey generateSecret();

	String getQrCodeUrl(GoogleAuthenticatorKey secret, String username);

	boolean verifyCode(String secret, int code);

}
