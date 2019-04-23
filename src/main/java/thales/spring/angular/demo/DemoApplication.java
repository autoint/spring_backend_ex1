package thales.spring.angular.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Role;
import thales.spring.angular.demo.domain.RoleEnum;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.repositories.AccountRepository;
import thales.spring.angular.demo.repositories.AdvisorRepository;
import thales.spring.angular.demo.repositories.ClientRepository;
import thales.spring.angular.demo.repositories.RoleRepository;
import thales.spring.angular.demo.repositories.UserRepository;
import thales.spring.angular.demo.services.UserService;

@SpringBootApplication
public class DemoApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);

	@Autowired
	ClientRepository repoClient;

	@Autowired
	AccountRepository repoAccount;

	@Autowired
	AdvisorRepository repoAdvisor;

	@Autowired
	UserRepository repoUser;

	@Autowired
	RoleRepository repoRole;

	@Autowired
	private UserService serviceUser;

	@Autowired
	SetUpProperties setup;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	ApplicationRunner init() {
		return args -> {
			createRoleIfNotFound(RoleEnum.USER);
			createRoleIfNotFound(RoleEnum.ADMIN);

			initAdvisors();

			if (setup.isLoadDataTest()) {
				initUsersTest();
			}
		};
	}
	
	private List<String> initWordLists() throws IOException, URISyntaxException  {
		List<String> wordlist=new ArrayList<String>();
		
		InputStream inputStream = TypeReference.class.getResourceAsStream("/files/wordlist.txt");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	wordlist.add(line);
	        }
	    }
		
		return wordlist;
	}

	@Transactional
	private final Role createRoleIfNotFound(final RoleEnum typeRole) {
		Role role = repoRole.findByRole(typeRole);
		if (role == null) {
			role = new Role(typeRole);
		}
		role = repoRole.save(role);
		return role;
	}

	private void initAdvisors() throws IOException, JsonParseException, JsonMappingException {
		// read json and write to db
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Advisor>> typeReference = new TypeReference<List<Advisor>>() {
		};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/json/advisors.json");

		List<Advisor> advisors = mapper.readValue(inputStream, typeReference);

		repoAdvisor.saveAll(advisors);

		LOGGER.info("ADVISORS SAVED");
		repoAdvisor.findAll().forEach(System.out::println);

	}

	private void initUsersTest() throws IOException, JsonParseException, JsonMappingException, URISyntaxException {
		// read json and write to db
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<UserDto>> typeReference = new TypeReference<List<UserDto>>() {
		};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/json/usersdto.json");

		List<UserDto> users = mapper.readValue(inputStream, typeReference);

		List<String> wordLists = initWordLists();
		SecureRandom rand = new SecureRandom();
		int i = 0;
		for (UserDto userDto : users) {
			serviceUser.registerNewUserAccount(userDto);
		}

		LOGGER.info("USERS DTO SAVED");
		List<Long> advisorIds = repoAdvisor.findAllIds();
		List<Long> accountIds = repoAccount.findAllIds();
		
		List<Long> userIds = repoUser.findAllIds();
		List<Long> clientIds = repoClient.findAllIds();
		
		int nbAdvisors = advisorIds.size();
		int nbAccounts = accountIds.size();
		int nbUsers = userIds.size();
		int nbClients = clientIds.size();
		
		LOGGER.info("NOMBRE DE CONSEILLERS="+nbAdvisors);
		LOGGER.info("NOMBRE DE COMPTES    ="+nbAccounts);
		LOGGER.info("NOMBRE DE USERS      ="+nbUsers);
		LOGGER.info("NOMBRE DE CLIENTS    ="+nbClients);
	}

	private String lowerCaseWithoutAccents(String value) {
		String s = value.toLowerCase();
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		return s;
	}
}
