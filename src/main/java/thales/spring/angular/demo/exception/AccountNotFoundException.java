package thales.spring.angular.demo.exception;

public final class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3533703346229194048L;

	public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(final String message) {
        super(message);
    }

    public AccountNotFoundException(final Throwable cause) {
        super(cause);
    }
}
