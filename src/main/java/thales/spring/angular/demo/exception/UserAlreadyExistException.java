package thales.spring.angular.demo.exception;

public final class UserAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 490835231255414152L;

	public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(final String message) {
        super(message);
    }

    public UserAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
