package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;

import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.domain.UserSerializer;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
	private static final Logger LOGGER_HONEYPOT = LoggerFactory.getLogger("honeypot");

	@Autowired
	private UserService userService;

	@Autowired
	private ClientService clientService;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<User>> getUsers() {
		return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		LOGGER_FILE.info("getUserById");
		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		LOGGER_FILE.info("UPDATE USER");
		userService.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Void> updateUser(@RequestBody User user) {
		LOGGER_FILE.info("UPDATE USER");
		userService.update(user);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<String> loginResponseCreation(User user, HttpServletResponse response)
			throws JsonProcessingException {
		// Serialisation user et set cookie user
		UserSerializer serializer = new UserSerializer(user.getClient().getIdClient(), user);
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		// Serialize user object into JSON format
		String userSerialize = mapper.writeValueAsString(serializer);
		// Encode json to base64 and store it into cookie <user>
		Cookie cookie = new Cookie("user", Base64.getEncoder().encodeToString(userSerialize.getBytes()));
		// Change path to root for id rest path
		cookie.setPath("/");
		response.addCookie(cookie);
		// Retrieve token and response with json informations
		String token = user.getToken();
		return new ResponseEntity<>("{\"status\":\"OK\",\"token\":\"" + token + "\"}", HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam("email") String email, @RequestParam("password") String password, HttpServletResponse response)
			throws JsonProcessingException {
		LOGGER_FILE.info("LOGIN USER - " + email); //
		User user = userService.findByEmail(email);
		if (user != null && user.getPassword().equals(password)) {
			return this.loginResponseCreation(user, response);
		}
		return new ResponseEntity<>("{\"status\":\"KO\"}", HttpStatus.OK);
	}

	@GetMapping("/useragentConnection")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String> userAgentConnection(HttpServletRequest request, HttpServletResponse response)
			throws JsonProcessingException {
		String userAgent = request.getHeader("User-Agent");
		System.out.println(userAgent);
		if (userAgent.equals("admin")) {
			Optional<User> user = userService.findById(1L);
			if (user.isPresent()) {
				String token = request.getHeader("Authorization");
				User object = user.get();
				if (object.getToken().equals(token)) {
					return new ResponseEntity<>("{\"status\":\"ADMIN\"}", HttpStatus.OK);
				} else {
					return this.loginResponseCreation(object, response);
				}
			}
			return new ResponseEntity<>("{\"status\":\"KO\"}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"status\":\"KO\"}", HttpStatus.OK);
		}
	}

	@GetMapping("/getinfos")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String> deserializeUser(@CookieValue(name = "user", defaultValue = "") String cookieUser,
			HttpServletRequest request) throws IOException {
		User user = userService.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			LOGGER_FILE.info("DESERIALIZE USER");
			if (cookieUser.isEmpty()) {
				// if <user> cookie not set print error message
				return new ResponseEntity<>("Error cookie <user> not set", HttpStatus.NOT_FOUND);
			} else {
				ObjectMapper mapper = new ObjectMapper();
				// Decode cookie value
				String jsonCookie = new String(Base64.getDecoder().decode(cookieUser));
				// Deserialize jsonCookie
				UserSerializer serializerObject = mapper.readValue(jsonCookie, UserSerializer.class);
				// search client object for set user client
				Optional<Client> client = clientService.findById(serializerObject.getIdClient());
				if (client.isPresent()) {
					Client clientObject = client.get();
					serializerObject.getUser().setClient(clientObject);
					return new ResponseEntity<>(serializerObject.getUser().clientInformations(), HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Error : user not associated to a client", HttpStatus.NOT_FOUND);
				}
			}
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}

	}

	@GetMapping("/honeypot")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Void> honeypot(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		LOGGER_HONEYPOT.info("POTENTIAL ATTACK : IP<" + ip + ">.");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/verify")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String> verifyToken(HttpServletRequest request) {
		LOGGER_FILE.info("VERIFY USER TOKEN");
		User user = userService.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			return new ResponseEntity<>("{\"status\":\"OK\"}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"status\":\"KO\"}", HttpStatus.OK);
		}
	}
}
