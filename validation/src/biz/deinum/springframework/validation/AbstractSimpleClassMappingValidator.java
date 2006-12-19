package biz.deinum.springframework.validation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * This class takes two parameters a supportedClass and a field. The supportedClass is the class
 * supported by this validator and the field is the field to validate.
 * 
 * @author mdeinum
 *
 */
public abstract class AbstractSimpleClassMappingValidator extends AbstractValidator implements InitializingBean {

	private Class supportedClass;
	protected String field;
	
	public final boolean supports(final Class clazz) {
		return supportedClass.isAssignableFrom(clazz);
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(supportedClass, "'SupportedClass' must be set!");
		Assert.notNull(field, "'Field' must be set!");		
	}
	
	protected Object getValue(Errors errors) {
		return errors.getFieldValue(field);
	}
	
	public final void setSupportedClass(final Class supportedClass) {
		this.supportedClass=supportedClass;
	}
	
	public final void setField(final String field) {
		this.field=field;
	}
}
