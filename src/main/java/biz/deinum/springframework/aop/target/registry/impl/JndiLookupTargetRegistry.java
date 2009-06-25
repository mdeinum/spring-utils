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
 * @author Marten Deinum
 * @since 1.1
 * 
 * @see JndiObjectLocator
 */
public class JndiLookupTargetRegistry extends JndiLocatorSupport implements TargetRegistry {

	public Object getTarget(String context) {
		try {
			return lookup(context);
		} catch (NamingException e) {
			throw new TargetLookupFailureException("Failed to lookup target for context '"+context+"'", e);
		}
	}
	
}
