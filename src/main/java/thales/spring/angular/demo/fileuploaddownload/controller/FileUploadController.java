package thales.spring.angular.demo.fileuploaddownload.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import thales.spring.angular.demo.fileuploaddownload.payload.Response;
import thales.spring.angular.demo.fileuploaddownload.service.FileStorageService;

@RestController
public class FileUploadController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public Response uploadFile(@RequestParam("file") MultipartFile file) {		
		LOGGER.info("uploadFile");
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();

		return new Response(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	public List<Response> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
	}
}
