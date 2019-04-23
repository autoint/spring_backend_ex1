package thales.spring.angular.demo.fileuploaddownload.exception;

public class FileStorageException extends RuntimeException {

	private static final long serialVersionUID = -8457879450624004767L;

	public FileStorageException(String message) {
		super(message);
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
