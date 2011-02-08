package biz.deinum.springframework.aop.target.registry.impl;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.jndi.JndiTemplate;

import biz.deinum.springframework.aop.target.registry.TargetRegistry;

/**
 * {@link TargetRegistry} implementation which delegates the lookup to a
 * {@link JndiTemplate}. 
 * 
 * Can optionally include a <code>prefix</code> and <code>suffix</code> to be 
 * added to make up the actual JNDI name.
 * 
 * @author Marten Deinum
 * @since 1.1
 * 
 * @see JndiObjectLocator
 */
public class JndiLookupTargetRegistry<T> extends JndiLocatorSupport implements TargetRegistry<T> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private String prefix = "";

	private String suffix = "";
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see #lookup(String)
	 */
	@SuppressWarnings("unchecked")
	public T getTarget(String context) {
		T target = null; 
		try {
			String jndiName = getJndiName(context);
			target = (T) lookup(jndiName);
		} catch (NamingException e) {
			//Log exception but don't rethrow, that would break the TargetRegistry contract.
			logger.error("Error looking up target for context '{}'", context , e);
		}
		return target;
	}

	/**
	 * Set the prefix that gets prepended to the context name when building the
	 * jndiname.
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Set the suffix that gets appended to the context name when building the
	 * jndiname.
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	protected String getJndiName(String context) {
		return this.prefix + context + this.suffix;
	}
	
	
}
