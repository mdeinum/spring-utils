package biz.deinum.springframework.aop.target;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
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
 */
public class ContextSwappableTargetSource implements TargetSource,
		InitializingBean {
	private final Logger logger = LoggerFactory.getLogger(ContextSwappableTargetSource.class);
	private Map targets = Collections.synchronizedMap(new HashMap());
	private Class targetClass;
	private boolean alwaysReturnTarget = false;
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
	 * this context name is used to lookup the desired target. When none
	 * is found we return the default target. 
	 * 
	 * If the targetClass is of a invalid type we throw a {@link BeanNotOfRequiredTypeException}
	 * 
	 * @see ContextHolder
	 */

	public Object getTarget() throws Exception {
        // Determine the current context name from theclass that holds the
        // context name for the current thread.
        String contextName = ContextHolder.getContext();
    	logger.debug("Current context: '{}'", contextName);
        
        Object target = targets.get(contextName);
        if (target == null && alwaysReturnTarget) {
    		logger.debug("Return default target for context '{}'", contextName);
    		target = defaultTarget;
        } else {
        	logger.error("Cannot locate a target for context '{}'", contextName);
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

	public void releaseTarget(Object arg0) throws Exception {}

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
	
	public final void setTargets(final Map targets) {
		this.targets.clear();
		this.targets.putAll(targets);
	} 

}
