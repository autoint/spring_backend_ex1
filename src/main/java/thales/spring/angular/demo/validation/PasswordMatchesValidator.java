package thales.spring.angular.demo.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import thales.spring.angular.demo.dto.UserDto;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
    	//TODO NOTHING
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserDto user = (UserDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }

}
