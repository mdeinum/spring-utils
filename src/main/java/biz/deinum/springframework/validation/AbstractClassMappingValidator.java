package biz.deinum.springframework.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;

/**
 * 
 * @author mdeinum
 */
public abstract class AbstractClassMappingValidator extends AbstractValidator {
    private Map mappings = new HashMap();

    /**
     * Check all the mappings assigned to this validator. If one of the classes
     * is assignable to this object return true.
     * 
     * @return <code>true</code> when supported, <code>false</code> otherwise
     */
    public final boolean supports(final Class clazz) {
    	Iterator keyIter = mappings.keySet().iterator();
    	boolean supports = false;
    	while (keyIter.hasNext() && !supports) {
    		Class targetClazz = (Class) keyIter.next();
    		supports = ClassUtils.isAssignable(targetClazz, clazz);
    	}
    	return supports;
    }

    /**
     * Gets the fieldname from the configured map. 
     * @param target
     * @return
     */
    protected final String getFieldName(final Object target) {
    	Iterator keyIter = mappings.keySet().iterator();
    	String fieldName = null;
    	while (keyIter.hasNext() && fieldName == null) {
    		Class targetClazz = (Class) keyIter.next();
    		if (ClassUtils.isAssignable(targetClazz, target.getClass())) {
    			fieldName = (String) mappings.get(targetClazz);
    		}
    	}
    	
    	if (fieldName == null) {
    		throw new IllegalStateException("Cannot find fieldname for class " + target.getClass().getName() + ". Class is not compatible with declared types. ["+mappings+"]");    		
    	}
    	return fieldName;
        
    }

    protected final Object getValue(final Object target, final Errors errors) {
    	String field = getFieldName(target);
    	return errors.getFieldValue(field);
    }
    
    public final void setMappings(final Map mappings) {
        this.mappings = mappings;
    }
}
