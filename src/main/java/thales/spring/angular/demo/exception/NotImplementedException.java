package thales.spring.angular.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 6167091272950188556L;

	public NotImplementedException() {
		super("This function is not impemented.");
	}
	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}
}
