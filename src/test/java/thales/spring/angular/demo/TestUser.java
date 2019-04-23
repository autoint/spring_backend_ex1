package thales.spring.angular.demo;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.NonNull;

@Entity
public class TestUser {
	@Id 
	@GeneratedValue
	private Long id;
	@NonNull
	private String login;
	@NonNull
	@Convert(converter = TestCryptoConverter.class)
	private String password;
}


