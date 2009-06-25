package biz.deinum.springframework.aop.target.registry.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import biz.deinum.springframework.aop.target.registry.AbstractTargetRegistry;

/**
 * TargetRegistry which retrieves a bean from the application context.
 * @author Marten Deinum
 * @version 1.0
 */
public class ApplicationContextTargetRegistry extends AbstractTargetRegistry implements ApplicationContextAware {

	private final Logger logger = LoggerFactory.getLogger(ApplicationContextTargetRegistry.class);
			
	private ApplicationContext appContext;
	private String prefix = "";  
	private String suffix = "";
	
	public void setPrefix(final String prefix) {
		Assert.notNull(prefix, "When setting prefix, prefix cannot be null!");
		this.prefix=prefix;
	}
	public void setSuffix(final String suffix) {
		Assert.notNull(prefix, "When setting suffix, suffix cannot be null!");
		this.suffix=suffix;
	}

	private String getTargetName(String context) {
		final String beanName = prefix + context + suffix;
		logger.debug("TargetName: {}", beanName);
		return beanName;
	}
	/**
	 * Gets the target from the ApplicationContext. The name of the bean is being
	 * constructed with the configured <code>prefix</code> and <code>suffix</code>.
	 * 
	 * @return the found object or <code>null</code>
	 */
	protected Object getTargetInternal(String context) {
		String beanName = getTargetName(context);
		Object target = null;
		try {
			logger.debug("Retrieving bean '{}' from ApplicationContext.", beanName);
			target = this.appContext.getBean(beanName);
		} catch (BeansException be) {
			logger.warn("Could not retrieve bean '"+context+"'", be);	
		}
		return target;
	}
	
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.appContext=context;
	}
}
