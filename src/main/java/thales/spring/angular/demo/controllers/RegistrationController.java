package thales.spring.angular.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.domain.UserSerializer;
import thales.spring.angular.demo.dto.UserDto;
import thales.spring.angular.demo.exception.UserAlreadyExistException;
import thales.spring.angular.demo.services.UserService;

@RestController
public class RegistrationController {
	private final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);
	private final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
	
	@Autowired
	private UserService service;
	
	@PostMapping(value = "/api/registration")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String>  registerUserAccount(@RequestBody @Valid final UserDto accountDto, HttpServletResponse response) throws JsonProcessingException {
        LOGGER.info("Registering user account with information: {}", accountDto);
        LOGGER_FILE.info("USER REGISTRATION");
        final User registered;
        try {
            registered = service.registerNewUserAccount(accountDto);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"Cet e-mail existe déjà.\"}", HttpStatus.OK);
        }
        if (registered == null) {
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\\\":\"La création d'un nouvel utilisateur a échoué.\"}", HttpStatus.OK);
        } else {
            //Serialisation user et set cookie user
            UserSerializer serializer = new UserSerializer(registered.getClient().getIdClient(), registered);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Serialize user object into JSON format
            String userSerialize = mapper.writeValueAsString(serializer);
            // Encode json to base64 and store it into cookie <user>
            Cookie cookie = new Cookie("user", Base64.getEncoder().encodeToString(userSerialize.getBytes()));
            // Change path to root for id rest path
            cookie.setPath("/");
            response.addCookie(cookie);
            return new ResponseEntity<>("{\"status\":\"OK\", \"token\":\"" + registered.getToken() + "\"}", HttpStatus.OK);
        }
    }
}
