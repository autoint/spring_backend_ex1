package thales.spring.angular.demo.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/shell")
public class WebshellController {
	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
	@Autowired
	private UserService serviceUser;

	// @GetMapping(value = "/{regex}")
	@PostMapping
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<List<String>> shell(@RequestBody String regex, HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			LOGGER_FILE.info("Webshell");
			List<String> output = new ArrayList<String>();

			int result = -1;
			System.out.println(regex);
			try {
				Runtime rt = Runtime.getRuntime();
				Process proc;
				proc = rt.exec(new String[] { "sh", "-c", regex });
				result = proc.waitFor();
				try (InputStream in = (result == 0) ? proc.getInputStream() : proc.getErrorStream(); BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));) {
					String s;
					long l = 0L;
					while ((s = br.readLine()) != null) {
						output.add(s);
					}
				}
			} catch (IOException e) {
				LOGGER_FILE.info(e.getMessage());
			} catch (InterruptedException e) {
				LOGGER_FILE.info(e.getMessage());
			}

			if (output.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(output, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
}
