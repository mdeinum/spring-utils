package biz.deinum.springframework.validation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * This validator will delegate each of it's child validators.
 *
 * @author mdeinum (thanks to Colin Yates)
 *
 */
public final class CompositeValidator implements Validator {
    private final List<Validator> validators = new LinkedList<Validator>();

    public CompositeValidator(final Validator[] validators) {
    	this(Arrays.asList(validators));
    }
    
    public CompositeValidator(final List<Validator> validators) {
    	super();
    	this.validators.addAll(validators);
    }
    
    /**
     * Will return true if this class is in the specified map.
     */
    @SuppressWarnings("rawtypes")
	public boolean supports(final Class clazz) {
    	for (Validator v : validators) {
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
    	for (Validator v : validators) {
            if (v.supports(target.getClass())) {
               v.validate(target, errors);
            }
    	}
    }
}
