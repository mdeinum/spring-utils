package biz.deinum.springframework.aop.target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import biz.deinum.springframework.core.ContextHolder;

/**
 * TargetSource which returns the correct target based on the current context set in the {@link biz.deinum.springframework.core.ContextHolder}. 
 * If no context is found a {@link TargetLookupFailureException} is thrown or the <code>defaultTarget</code> is returned
 * , depending on the setting of the alwaysReturnTarget property (default is false);
 * 
 * @author M. Deinum
 * @version 1.0
 * @see ContextHolder
 * @see TargetSource
 * @see TargetRegistry
 */
public class ContextSwappableTargetSource implements TargetSource,
		InitializingBean {
	private final Logger logger = LoggerFactory.getLogger(ContextSwappableTargetSource.class);
	
	/** The TargetRegistry used to lookup the desired target*/
	private TargetRegistry registry;
	
	/** The type of class this TargetSource supports */
	private Class targetClass;
	
	/** Should we alsways return a value, if <code>true</code>, defaultTarget must also be set */
	private boolean alwaysReturnTarget = false;
	
	/** The defaultObjct to return if <code>alwaysReturnTarget</code> is true */
	private Object defaultTarget;
	
	/**
	 * Constructor for the {@link ContextSwappableTargetSource} class. It takes a 
	 * Class as a parameter.
	 * 
	 * @param targetClass The Class which this TargetSource represents.
	 */
	public ContextSwappableTargetSource(Class targetClass) {
		super();
		this.targetClass=targetClass;
	}
	
	/**
	 * Locate and return the sessionfactory for the current context.
	 * 
	 * First we lookup the context name from the {@link ContextHolder}
	 * this context name is used to lookup the desired target. When no
	 * target is being found an {@link TargetLookupFailureException} is thrown.
	 * 
	 * If the targetClass is of a invalid type we throw a {@link TargetLookupFailureException}
	 * 
	 * @see ContextHolder
	 * @throws TargetLookupFailureException
	 */

	public Object getTarget() throws Exception {
        String contextName = ContextHolder.getContext();
    	logger.debug("Current context: '{}'", contextName);
        
        Object target = getTarget(contextName);

        if (target == null) {
        	logger.error("Cannot locate a target of type '{}' for context '{}'", targetClass.getName(), contextName);
        	throw new TargetLookupFailureException("Cannot locate a target for context '"+contextName+"'");
        }
        
        if (!targetClass.isAssignableFrom(target.getClass())) {
        	throw new TargetLookupFailureException("The target for '"+contextName+"' is not of the required type." + 
        			"Expected '"+targetClass.getName()+"' and got '"+target.getClass().getName()+"'");
        }
        return target;

	}

	public final Class getTargetClass() {
		return targetClass;
	}

	public final boolean isStatic() {
		return false;
	}
	
	/**
	 * Gets the targetobject from the configured {@link TargetRegistry}. If no target is found and
	 * <code>alwaysReturnTarget</code> is set to <code>true</code>, the <code>defaultTarget</code> is used
	 * as the targetObject, else <code>null</code> is being returned.
	 * 
	 * @param context
	 * @return targetObject or <code>null</code>
	 * @see TargetRegistry#getTarget(String)
	 */
	private Object getTarget(final String context) {
		Object target = registry.getTarget(context);
		if (target == null && alwaysReturnTarget) {
			logger.debug("Return default target for context '{}'", context);
			target = defaultTarget;
		}
		return target;
	}

	public void releaseTarget(final Object target) throws Exception {}

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(targetClass, "TargetClass property must be set!");
		
		if (alwaysReturnTarget && defaultTarget == null) {
			throw new IllegalStateException("The defaultTarget property is null, while alwaysReturnTarget is set to true. " +
					"When alwaysReturnTarget is set to true a defaultTarget must be set!");
		}
	}
	
	public final void setAlwaysReturnTarget(final boolean alwaysReturnTarget) {
		this.alwaysReturnTarget=alwaysReturnTarget;
	}
	
    public final void setDefaultTarget(final Object defaultTarget) {
        this.defaultTarget=defaultTarget;
    }
	
    public final void setTargetRegistry(final TargetRegistry registry) {
    	this.registry=registry;
    }

}
