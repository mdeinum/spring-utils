package biz.deinum.springframework.validation;

import org.springframework.validation.Validator;

public abstract class AbstractValidator implements Validator{
    /** Prefix Strings which are added to the field names */
    protected static final String REQUIRED = "required";
    protected static final String INVALID = "invalid";

    protected boolean allowEmpty = false;
    
    public final void setAllowEmpty(final boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }
}
