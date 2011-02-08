package biz.deinum.springframework.aop.target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;
import org.springframework.util.Assert;

import biz.deinum.springframework.aop.target.registry.TargetPostProcessor;
import biz.deinum.springframework.aop.target.registry.TargetRegistry;
import biz.deinum.springframework.aop.target.registry.impl.BeanFactoryTargetRegistry;
import biz.deinum.springframework.core.ContextHolder;

/**
 * TargetSource which returns the correct target based on the current context set in the {@link biz.deinum.springframework.core.ContextHolder}. 
 * If no context is found a {@link TargetLookupFailureException} is thrown or the <code>defaultTarget</code> is returned
 * , depending on the setting of the alwaysReturnTarget property (default is false);
 * 
 * By default this class delegates the detection of target to a {@link BeanFactoryTargetRegistry}. 
 * Multiple target registries can be registered on this bean, either manually or by just declaring
 * them in the ApplicationContext. A best effort is made to lookup the TargetRegistries from the
 * ApplicatonContext.
 *  
 * 
 * @author M. Deinum
 * @version 1.2
 * @see ContextHolder
 * @see TargetSource
 * @see TargetRegistry
 * @see TargetPostProcessor
 */
public class ContextSwappableTargetSource implements TargetSource, InitializingBean, ApplicationContextAware {
	private final Logger logger = LoggerFactory.getLogger(ContextSwappableTargetSource.class);
	
	/** The TargetRegistries used to lookup the desired target */ 
	private final List<TargetRegistry<?>> registries = new LinkedList<TargetRegistry<?>>();
	
	/** The type of class this TargetSource supports */
	private Class<?> targetClass;
	
	/** Should we always return a value, if <code>true</code>, defaultTarget must also be set */
	private boolean alwaysReturnTarget = false;
	
	/** The defaultObject to return if <code>alwaysReturnTarget</code> is true */
	private Object defaultTarget;
	
	/** The targetPostProcessor to use (optional). */
	private TargetPostProcessor targetPostProcessor;
	
	/** The ApplicationContext we are defined in */
	private ApplicationContext context;
	
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
	 * Locate and return the target for the current context.
	 * 
	 * First we lookup the context name from the {@link ContextHolder}
	 * this context name is used to lookup the desired target. When no
	 * target is found a {@link TargetLookupFailureException} is thrown.
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
        
        if (isEligibleForPostProcessing(target)) {
        	targetPostProcessor.postProcess(target);
        }
        
        return target;

	}

	/**
	 * Checks if the given <em>target</em> is eligible for post processing. A
	 * target is eligible if it is not the same as the defaultTarget and not
	 * null. The final check is done against the {@link TargetPostProcessor}.
	 * Subclasses may override this method.
	 *  
	 * @param target
	 * @return
	 */
	protected boolean isEligibleForPostProcessing(Object target) {
		boolean eligible = false;
		if (target != null && target != defaultTarget) {
			eligible = (targetPostProcessor != null && targetPostProcessor.supports(target.getClass()));
		}
		return eligible;
	}
	
	public final Class getTargetClass() {
		return targetClass;
	}

	public final boolean isStatic() {
		return false;
	}
	
	/**
	 * Gets the targetobject from the configured {@link TargetRegistry}. If no
	 * target is found and <code>alwaysReturnTarget</code> is set to <code>
	 * true</code>, the <code>defaultTarget</code> is used as the targetObject,
	 * else <code>null</code> is being returned. 
	 * 
	 * @param context
	 * @return targetObject or <code>null</code>
	 * @throws TargetLookupFailureException if nothing can be returned
	 * 
	 * @see TargetRegistry#getTarget(String)
	 */
	private Object getTarget(final String context) {
		Object target = resolveTarget(context);
		if (target == null && alwaysReturnTarget) {
			logger.debug("Return default target for context '{}'", context);
			target = defaultTarget;
		}
		return target;
	}

	protected Object resolveTarget(final String context) {
		Object target = null;
		for (Iterator it = this.registries.iterator(); it.hasNext();) {
			TargetRegistry registry = (TargetRegistry) it.next();
			logger.debug("Using '{}' to lookup '{}'.", registry, context);
			target= registry.getTarget(context);
			if (target != null) {
				break;
			}
		}
		return target;
	}
	
	public void releaseTarget(final Object target) throws Exception {}

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(targetClass, "TargetClass property must be set!");
		
		initTargetRegistries();
		
		if (alwaysReturnTarget) {
			Assert.notNull(defaultTarget, "The defaultTarget property is null, while alwaysReturnTarget is set to true. " +
					"When alwaysReturnTarget is set to true a defaultTarget must be set!");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initTargetRegistries() {
		if (this.registries.isEmpty()) {
			Map<String, TargetRegistry<?>> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, TargetRegistry.class, true, false);
			if (!matchingBeans.isEmpty()) {
				this.registries.addAll(matchingBeans.values());
				Collections.sort(this.registries, new OrderComparator());
			} else {
				BeanFactoryTargetRegistry<?> registry = new BeanFactoryTargetRegistry();
				registry.setBeanFactory(this.context);
				this.registries.add(registry);
			}
		}
	}
	
	public final void setAlwaysReturnTarget(final boolean alwaysReturnTarget) {
		this.alwaysReturnTarget=alwaysReturnTarget;
	}
	
    public final void setDefaultTarget(final Object defaultTarget) {
        this.defaultTarget=defaultTarget;
    }
	
    public final void setTargetRegistry(final TargetRegistry<?> registry) {
    	this.registries.clear();
    	this.registries.add(registry);
    }
    
    public final void setTargetRegistries(final List<TargetRegistry<?>> registries) {
    	this.registries.addAll(registries);
    }
    
    public final void setTargetPostProcessor(final TargetPostProcessor targetPostProcessor) {
    	this.targetPostProcessor=targetPostProcessor;
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context=applicationContext;
    }

    List<TargetRegistry<?>> getTargetRegistries() {
    	return this.registries;
    }
    
}
