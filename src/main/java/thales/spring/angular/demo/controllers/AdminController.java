package thales.spring.angular.demo.controllers;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    //Logger for admin controller
    private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
    @Autowired
    private UserService serviceUser;
    
    //Api rest point for admin
    @GetMapping( produces = "text/plain; charset=UTF-8")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> adminPanel(HttpServletRequest request) {
        User user = serviceUser.findByToken(request.getHeader("Token"));
        if (user != null) {
            LOGGER_FILE.info("ADMIN PANEL CONNEXION OK");
            return new ResponseEntity<String>("<h1>Congratzzzzz you are in admin area...</h1><img src='https://media.giphy.com/media/111ebonMs90YLu/giphy.gif'>", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }
}
