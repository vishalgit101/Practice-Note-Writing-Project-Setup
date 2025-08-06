package services;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

@Service
public class TotpServiceImp implements TotpService {
	// DI for gAuth
	private final GoogleAuthenticator gAuth;

	public TotpServiceImp(GoogleAuthenticator gAuth) {
		super();
		this.gAuth = gAuth;
	}
	
	public TotpServiceImp() { // both are different constructors, this was used as default constructor
		super();
		this.gAuth = new GoogleAuthenticator();
	}


	@Override
	public GoogleAuthenticatorKey generateSecret() { // GoogleAuthenticatorKey is a Class that represents a secretKey
		return gAuth.createCredentials(); // hover to see what kind of credentials are going to be created
	}
	
	@Override
	public String getQrCodeUrl(GoogleAuthenticatorKey secret, String username) { // going to generate QR url that will have an image that we embed on the frontend
		 String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("NotesVault", username, secret);
		//String qrCodeImageUrl = "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=" + URLEncoder.encode(otpAuthURL, StandardCharsets.UTF_8);
		return otpAuthURL;
	}
	
	@Override 
	public boolean verifyCode(String secret, int code) { // does the job verification
		return this.gAuth.authorize(secret, code);
	}
}
