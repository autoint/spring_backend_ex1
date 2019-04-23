package thales.spring.angular.demo.controllers;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.exception.AccountAlreadyExistException;
import thales.spring.angular.demo.exception.ClientNotFoundException;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.services.UserService;
import thales.spring.angular.demo.util.GenericResponse;

@RestController
@RequestMapping("/api/account")
public class AccountController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");

	@Autowired
	private AccountService service;

	@Autowired
	private ClientService serviceClient;
	@Autowired
	private UserService serviceUser;

	@GetMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Account>> getAccounts() {
		LOGGER_FILE.info("GETACCOUNTS");
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Account> getAccount(@PathVariable Long id) {
		LOGGER_FILE.info("GET ACCOUNT");
		Optional<Account> account = service.findById(id);
		if (account.isPresent()) {
			return new ResponseEntity<>(account.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(value = "/user")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<Collection<Account>> getUserAccounts(HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null)
			return new ResponseEntity<>(user.getClient().getAccounts(), HttpStatus.OK);
		else
			return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@GetMapping(value = "/rib", produces = "text/plain; charset=UTF-8")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<String> getCompteCourant(HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		LOGGER_FILE.info("RETRIEVE ACCOUNT");
		if (user != null) {
			for (Account account : user.getClient().getAccounts()) {
				if (account.getType().equals("Compte Courant")) {
					return new ResponseEntity<String>(account.getDocument(), HttpStatus.OK);
				}
			}
			return new ResponseEntity<>(null, HttpStatus.OK);
		} else
			return new ResponseEntity<>(null, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteAccounts(@PathVariable Long id) {
		LOGGER_FILE.info("DELETE ACCOUNT");
		service.deleteById(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<?> updateAccounts(@RequestBody Account account) {
		LOGGER_FILE.info("UPDATE ACCOUNT");
		return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
	}

	@PostMapping(value = "ok/{idClient}", produces = { "application/json", "application/xml" }, consumes = {
			"application/json", "application/xml" })
	public ResponseEntity<?> createAccount(@PathVariable Long idClient, @RequestBody Account account) {

		GenericResponse response = null;
		Client client = null;

		try {
			client = verifClient(idClient);
		} catch (ClientNotFoundException cnfe) {
			response = new GenericResponse(cnfe.getMessage(), "Client non trouvé");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			if (service.isExist(account.getIdAccount())) {
				throw new AccountAlreadyExistException(String.format("%s existant", account.getIdAccount()));
			}
			// APPELER JUSTE POUR AVOIR LA STRUCTURE XML D'UN COMPTE
			serializeToXML(account);
			account.setClient(client);
		} catch (IOException e) {
			response = new GenericResponse(e.getMessage(), "IOException");
		} catch (AccountAlreadyExistException e) {
			response = new GenericResponse(e.getMessage(), "Compte déjà existant");
		}

		if (response == null) {
			return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "xml")
	public ResponseEntity<?> serializeAccount(@RequestBody String xmlAccount) {

		Account account = deserializeFromXML(xmlAccount);

		return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
	}

	@PostMapping
	public ResponseEntity<?> buildAccount(@RequestBody String xmlAccount, HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			Long idClient = user.getClient().getIdClient();
			LOGGER.info("buildAccount for : " + idClient);

			GenericResponse response = null;
			Client client = null;
			try {
				client = verifClient(idClient);
			} catch (ClientNotFoundException cnfe) {
				LOGGER.info("buildAccount for ClientNotFoundException: " + idClient);
				response = new GenericResponse(cnfe.getMessage(), "Client non trouvé");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			List<Account> accounts = null;

			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder;
				Document doc;
				builder = factory.newDocumentBuilder();
				doc = builder.parse(new ByteArrayInputStream(xmlAccount.getBytes()));
				doc.getDocumentElement().normalize();

				NodeList listOfAccounts = doc.getElementsByTagName("account");
				int totalAccounts = listOfAccounts.getLength();

				accounts = new ArrayList<>(totalAccounts);

				for (int s = 0; s < totalAccounts; s++) {

					Node firstAccountNode = listOfAccounts.item(s);
					if (firstAccountNode.getNodeType() == Node.ELEMENT_NODE) {

						Element firstAccountElement = (Element) firstAccountNode;

						String idAccount = extractElementByName(firstAccountElement, "idAccount");
						if (service.isExist(Long.valueOf(idAccount))) {
							throw new AccountAlreadyExistException(String.format("%s existant", idAccount));
						}
						String type = extractElementByName(firstAccountElement, "type");
						String balance = extractElementByName(firstAccountElement, "balance");
						String bonus = extractElementByName(firstAccountElement, "rate");
						String statement = extractElementByName(firstAccountElement, "document");

						Account account = new Account();
						account.setIdAccount(Long.valueOf(idAccount));
						account.setBalance(Double.valueOf(balance));
						account.setRate(Double.valueOf(bonus));
						account.setType(type);
						account.setDocument(statement);
						account.setClient(client);
						accounts.add(account);
					}
				}
			} catch (ParserConfigurationException e) {
				response = new GenericResponse(e.getMessage(), "ParserConfigurationException");
			} catch (SAXException e) {
				response = new GenericResponse(e.getMessage(), "SAXException");
			} catch (IOException e) {
				response = new GenericResponse(e.getMessage(), "IOException");
			} catch (AccountAlreadyExistException e) {
				response = new GenericResponse(e.getMessage(), "Compte déjà existant");
			}

			if (response == null && accounts != null) {
				return new ResponseEntity<>(service.updateAll(accounts), HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(new GenericResponse("Vous n'êtes pas autorisé à accéder cette resource", "Echec de l'Authentification"), HttpStatus.BAD_REQUEST);
		}
	}

	private Client verifClient(Long idClient) {
		Optional<Client> oClient = serviceClient.findById(idClient);
		if (!oClient.isPresent()) {
			throw new ClientNotFoundException(String.format("%s inexistant", idClient));
		}
		return oClient.get();
	}

	private String extractElementByName(Element element, String name) {
		NodeList nameList = element.getElementsByTagName(name);
		Element nameElement = (Element) nameList.item(0);

		NodeList textFNList = nameElement.getChildNodes();
		String nameValue = ((Node) textFNList.item(0)).getNodeValue().trim();
		return nameValue;
	}

	private static void serializeToXML(Account account) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("/tmp/accounts.xml");
				XMLEncoder encoder = new XMLEncoder(fos)) {
			encoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					System.out.println("Exception! :" + e.toString());
				}
			});
			encoder.writeObject(account);
		}
	}

	private static Account deserializeFromXML(String xmlAccount) {
		Account decoded = null;
		LOGGER_FILE.info("deserializeFromXML ACCOUNT");
		LOGGER_FILE.info(xmlAccount);
		LOGGER_FILE.info("");
		try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlAccount.getBytes()))) {		 
		    decoded = (Account) decoder.readObject();	    	
	    }
		return decoded;
	}
}
