package biz.deinum.springframework.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;

/**
 * 
 * @author mdeinum
 */
public abstract class AbstractClassMappingValidator extends AbstractValidator {
    private Map<Class<?>, String> mappings = new HashMap<Class<?>, String>();

    /**
     * Check all the mappings assigned to this validator. If one of the classes
     * is assignable to this object return true.
     * 
     * @return <code>true</code> when supported, <code>false</code> otherwise
     */
    @SuppressWarnings("rawtypes")
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
    	String fieldName = null;
    	for (Entry<Class<?>, String> entry : mappings.entrySet()) {
    		if (ClassUtils.isAssignable(entry.getKey(), target.getClass())) {
    			fieldName = entry.getValue();
    			break;
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
    
    public final void setMappings(final Map<Class<?>, String> mappings) {
        this.mappings = mappings;
    }
}
