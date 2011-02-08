package biz.deinum.springframework.aop.target.registry.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import biz.deinum.springframework.aop.target.registry.AbstractTargetRegistry;

/**
 * TargetRegistry which retrieves a bean from the {@link BeanFactory}.
 * @author Marten Deinum
 * @version 1.0
 */
public class BeanFactoryTargetRegistry<T> extends AbstractTargetRegistry<T> implements BeanFactoryAware {

	private final Logger logger = LoggerFactory.getLogger(BeanFactoryTargetRegistry.class);
			
	private BeanFactory beanFactory;
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
	@SuppressWarnings("unchecked")
	protected T getTargetInternal(String context) {
		String beanName = getTargetName(context);
		T target = null;
		try {
			logger.debug("Retrieving bean '{}' from BeanFactory.", beanName);
			target = (T) this.beanFactory.getBean(beanName);
		} catch (BeansException be) {
			logger.warn("Could not retrieve bean '{}'", context, be);	
		}
		return target;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=beanFactory;
	}
	
}
