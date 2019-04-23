package thales.spring.angular.demo.fileuploaddownload.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thales.spring.angular.demo.domain.User;

import thales.spring.angular.demo.fileuploaddownload.service.FileStorageService;
import thales.spring.angular.demo.services.UserService;

@RestController
public class FileDownloadController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);
        
	@Autowired
	private FileStorageService fileStorageService;
	@Autowired
	private UserService serviceUser;

	@PostMapping("/api/downloadFile")
	public ResponseEntity<Resource> postDownloadFile(@RequestParam("filename") String fileName, HttpServletRequest request) {
		User user = serviceUser.findByToken(request.getHeader("Authorization"));
		if (user != null) {
			return download(fileName, request);
		} else {
			return new ResponseEntity<>(null, HttpStatus.CREATED);
		}
	}
	
	@GetMapping("/api/downloadFile/{fileName:.+}") 
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request)	{
		return download(fileName, request);
	}

	private ResponseEntity<Resource> download(String fileName, HttpServletRequest request) {
		LOGGER.info("downloadFile : {}", fileName);
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			LOGGER.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
                
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
}
