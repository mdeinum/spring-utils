package biz.deinum.springframework.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * This validator will delegate each of it's child validators.
 *
 * @author mdeinum (thanks to Colin Yates)
 *
 */
public final class CompositeValidator implements Validator {
    private final Validator[] validators;

    public CompositeValidator(final Validator[] validators) {
        super();
        this.validators=validators;
    }
    
    /**
     * Will return true if this class is in the specified map.
     */
    public boolean supports(final Class clazz) {
    	for (int i = 0; i < validators.length; i++) {
    		Validator v = validators[i];
            if (v.supports(clazz)) {
                return true;
            }
    	}
        return false;
    }

    /**
     * Validate the specified object using the validator registered for the object's class.
     */
    public void validate(final Object target, final Errors errors) {
    	for (int i = 0; i < validators.length; i++) {
    		Validator v = validators[i];
            if (v.supports(target.getClass())) {
               v.validate(target, errors);
            }
    	}
    }
}
