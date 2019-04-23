package thales.spring.angular.demo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "setup")
public class SetUpProperties {
	private String loadDataTest;
	
	public Boolean isLoadDataTest() {
		return Boolean.valueOf(loadDataTest);
	}
}