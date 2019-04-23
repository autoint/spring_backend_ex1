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

import thales.spring.angular.demo.domain.Customer;
import thales.spring.angular.demo.repositories.CustomerRepository;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");

	@Autowired
	private CustomerRepository repository;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Customer>> getCustomers() {
		LOGGER_FILE.info("GET CUSTOMERS");
		return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
		LOGGER_FILE.info("GET PORTFOLIO BY ID");
		Optional<Customer> customer = repository.findById(id);
		if (customer.isPresent()) {
			return new ResponseEntity<>(customer.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteCustomers(@PathVariable Long id) {
		LOGGER_FILE.info("DELETE CUSTOMER");
		repository.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<Void> updateCustomers(@RequestBody Customer customer) {
		LOGGER_FILE.info("UPDATE CUSTOMER");
		repository.save(customer);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PostMapping(produces = { "application/json", "application/xml" }, consumes = { "application/json",
			"application/xml" })
	public ResponseEntity<?> createCustomer(@RequestBody Customer customer)
			throws JsonParseException, JsonMappingException, IOException {
		return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);
	}
}
