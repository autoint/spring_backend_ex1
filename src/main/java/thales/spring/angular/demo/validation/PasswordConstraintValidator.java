package thales.spring.angular.demo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	@Override
	public void initialize(final ValidPassword arg0) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password != null && password.length() > 0) {
			return true;
		}
		context.disableDefaultConstraintViolation();
		return false;
	}
}
