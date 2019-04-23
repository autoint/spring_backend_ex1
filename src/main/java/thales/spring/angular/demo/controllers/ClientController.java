package thales.spring.angular.demo.controllers;

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

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.ClientService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {

	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");

	@Autowired
	private ClientService service;

	@Autowired
	private AccountService serviceAccount;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Client>> getClients() {
		LOGGER_FILE.info("GET CLIENTS");
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Client> getClient(@PathVariable Long id) {
		LOGGER_FILE.info("GET CLIENT BY ID");
		Optional<Client> client = service.findById(id);
		if (client.isPresent()) {
			return new ResponseEntity<>(client.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/{id}/accounts/{idc}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Account>> getAccountsByidClient(@PathVariable Long id, @PathVariable Long idc) {
		LOGGER_FILE.info("GET CONTRATS BY CLIENT ID");
		return new ResponseEntity<>(serviceAccount.getAccountByClientId(Long.toString(id)), HttpStatus.OK);
	}

//	@GetMapping("/customer/{customerName}")
//	@CrossOrigin(origins = "http://localhost:4200")
//	public ResponseEntity<Collection<Client>> getClientsByName(@PathVariable String customerName) {
//		return new ResponseEntity<>(repository.getByAdvisorId(customerName.replace('+', ' ')), HttpStatus.OK);
//	}
//	
//	@GetMapping("/matricule/{id}")
//	@CrossOrigin(origins = "http://localhost:4200")
//	public ResponseEntity<Collection<Client>> getClientsByMatricule(@PathVariable String id) {
//		LOGGER.info("getClientsByMatricule");
//		return new ResponseEntity<>(repository.findByAdvisor(id), HttpStatus.OK);
//	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
		LOGGER_FILE.info("DELETE CLIENT");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<Void> updateClient(@RequestBody Client client) {
		LOGGER_FILE.info("UPDATE CLIENT");
		service.update(client);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> createClient(@RequestBody Client client) {
		LOGGER_FILE.info("CREATE CLIENT");
		return new ResponseEntity<>(service.update(client), HttpStatus.CREATED);
	}
}
