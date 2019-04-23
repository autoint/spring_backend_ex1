package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javax.servlet.http.HttpServletRequest;

import thales.spring.angular.demo.domain.Message;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.MessageService;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
	@Autowired
	private MessageService service;
	@Autowired
	private UserService serviceUser;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Message>> getMessages(HttpServletRequest request)
			throws InstantiationException, IllegalAccessException {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null && user.admin()) {
			LOGGER_FILE.info("GET MESSAGES");
			Collection<Message> messages = service.findByRead(false);
			for (Message message : messages) {
				message.setRead(true);
				service.update(message);
			}
			return new ResponseEntity<>(messages, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
	}

	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Message> getMessage(@PathVariable Long id) {
		LOGGER_FILE.info("GET MESSAGE");
		Optional<Message> message = service.findById(id);
		if (message.isPresent()) {
			return new ResponseEntity<>(message.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteMessages(@PathVariable Long id) {
		LOGGER_FILE.info("DELETE MESSAGE");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateMessages(@RequestBody Message message) {
		LOGGER_FILE.info("UPDATE MESSAGE");
		return new ResponseEntity<>(service.update(message), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> createMessage(@RequestBody Message message, HttpServletRequest request)
			throws JsonParseException, JsonMappingException, IOException {
		LOGGER_FILE.info("CREATE MESSAGE");
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			message.setFrom(user.getClient().getIdClient());
			return new ResponseEntity<>(service.update(message), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}
}
