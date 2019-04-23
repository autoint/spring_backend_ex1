package thales.spring.angular.demo.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.FilePDFReport;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.SearchPDFServiceImpl;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/pdf/search")
public class SearchPDFController {
	private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
	@Autowired
	private UserService serviceUser;

	@Autowired
	private SearchPDFServiceImpl searchService;

	@GetMapping(value = "/{regex}")
	@CrossOrigin(origins = "http://localhost:4200")
	public ResponseEntity<List<FilePDFReport>> getFiles(@PathVariable String regex, HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			LOGGER_FILE.info("SEARCH PDF");
			List<FilePDFReport> reports = searchService.getPDFsLike(regex);
			if (reports.isEmpty()) {
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

			}
			return new ResponseEntity<>(reports, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
}
