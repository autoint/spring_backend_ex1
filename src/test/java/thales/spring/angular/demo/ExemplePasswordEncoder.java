package thales.spring.angular.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class ExemplePasswordEncoder {
	
	public String bCryptPasswordEncoder(String plaintextPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
	
	public String pbkdf2PasswordEncoder(String plaintextPassword) {
		Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
		
	public String scryptPasswordEncoder(String plaintextPassword) {
		SCryptPasswordEncoder passwordEncoder = new SCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
	
	public String messageDigestPasswordEncoder(String plaintextPassword) {
		@SuppressWarnings("deprecation")
		MessageDigestPasswordEncoder passwordEncoder = new MessageDigestPasswordEncoder("MD5");
		String hashedPassword = passwordEncoder.encode(plaintextPassword);
		return hashedPassword;
	}
}
