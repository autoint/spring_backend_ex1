package thales.spring.angular.demo.controllers;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

import thales.spring.angular.demo.domain.Advisor;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.AdvisorService;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/advisor")
public class AdvisorController {

	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");

	@Autowired
	private AdvisorService service;

	@Autowired
	private ClientService serviceClient;
	@Autowired
	private UserService serviceUser;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Advisor>> getAdvisors() {
		LOGGER_FILE.info("GET ADVISORS");
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	/* CE QU'IL FAUT FAIRE */
	@GetMapping(value = "ok/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Advisor> getAdvisor(@PathVariable Long id, HttpServletRequest request) {
		LOGGER_FILE.info("GET ADVISOR");
		Optional<Advisor> advisor = service.findById(id);
		if (advisor.isPresent()) {
			return new ResponseEntity<>(advisor.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

	}

	/* CE QU'IL NE FAUT PAS FAIRE */
	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Advisor>> getAdvisorById(@PathVariable String id, HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			LOGGER_FILE.info("GET ADVISOR");
			List<Advisor> advisors = service.getAdvisorById(id);
			if (advisors.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(advisors, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/user")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Long> getClientAdvisorId(HttpServletRequest request) {
		LOGGER_FILE.info("GET CLIENT ADVISOR ID");
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			return new ResponseEntity<>(user.getClient().getAdvisor().getIdAdvisor(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	@GetMapping(value = "/{id}/clients")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Client>> getClients(@PathVariable Long id) {
		LOGGER_FILE.info("GET CLIENTS");
		return new ResponseEntity<>(serviceClient.findByAdvisor(id), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteAdvisors(@PathVariable Long id) {
		LOGGER_FILE.info("DELETE ADVISOR");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateAdvisors(@RequestBody Advisor advisor) {
		LOGGER_FILE.info("UPDATE ADVISOR");
		return new ResponseEntity<>(service.update(advisor), HttpStatus.CREATED);
	}

	@PostMapping(produces = { "application/json", "application/xml" }, consumes = { "application/json",
			"application/xml" })
	public ResponseEntity<?> createAdvisor(@RequestBody Advisor advisor)
			throws JsonParseException, JsonMappingException, IOException {
		LOGGER_FILE.info("CREATE ADVISOR");
		// APPELER JUSTE POUR AVOIR LA STRUCTURE XML D'UN CONSEILLER
		serializeToXML(advisor);
		return new ResponseEntity<>(service.update(advisor), HttpStatus.CREATED);
	}

	private static void serializeToXML(Advisor advisor) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("/tmp/advisors.xml");
				XMLEncoder encoder = new XMLEncoder(fos)) {
			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					System.out.println("Exception! :" + e.toString());
				}
			});
			encoder.writeObject(advisor);
		}
	}

	private static Advisor deserializeFromXML(String xmlAdvisor) {
		Advisor decoded = null;

		try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlAdvisor.getBytes()))) {
			decoded = (Advisor) decoder.readObject();
		}
		return decoded;
	}
}
