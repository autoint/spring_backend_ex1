package thales.spring.angular.demo.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import thales.spring.angular.demo.domain.User;
import thales.spring.angular.demo.services.UserService;

@RestController
@RequestMapping("/api/log")
public class LogController {
    
    private static final Logger LOGGER_FILE = LoggerFactory.getLogger("ControllerInfoLogger");
    
    @Autowired
    private UserService serviceUser;
    
    @GetMapping("/{name}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<String> getLogs(@PathVariable String name, HttpServletRequest request) throws InstantiationException, IllegalAccessException, IOException {
        User user = serviceUser.findByToken(request.getHeader("Authorization"));
        if (user != null) {
            LOGGER_FILE.info("GET LOGS");
            byte[] encoded = Files.readAllBytes(Paths.get("/var/www/logs/" + name + ".log"));
            return new ResponseEntity<>(new String(encoded), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
