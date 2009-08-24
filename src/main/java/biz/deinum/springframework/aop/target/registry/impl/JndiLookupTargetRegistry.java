package biz.deinum.springframework.aop.target.registry.impl;

import javax.naming.NamingException;

import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.JndiObjectLocator;
import org.springframework.jndi.JndiTemplate;

import biz.deinum.springframework.aop.target.TargetLookupFailureException;
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
public class JndiLookupTargetRegistry extends JndiLocatorSupport implements TargetRegistry {

	private String prefix = "";

	private String suffix = "";
	
	
	public Object getTarget(String context) {
		try {
			String jndiName = getJndiName(context);
			return lookup(jndiName);
		} catch (NamingException e) {
			throw new TargetLookupFailureException("Failed to lookup target for context '"+context+"'", e);
		}
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
