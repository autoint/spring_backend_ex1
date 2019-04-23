package thales.spring.angular.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImpossibleActionException extends RuntimeException {

	private static final long serialVersionUID = 6167091272950188556L;

	public ImpossibleActionException() {
		super("This action is impossible");
	}
	public ImpossibleActionException(String message) {
		super(message);
	}

	public ImpossibleActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
