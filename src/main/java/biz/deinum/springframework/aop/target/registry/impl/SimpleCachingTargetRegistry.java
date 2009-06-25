package biz.deinum.springframework.aop.target.registry.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import biz.deinum.springframework.aop.target.registry.TargetRegistry;

/**
 * {@link TargetRegistry} implementation that does some simple caching.
 * It first checks the internal cache it it hasn't found a target it checks
 * the delegate TargetRegistry for a target, if found it is registered in the
 * internal cache for future use.
 * 
 * @author Marten Deinum
 * @since 1.2.0
 *
 */
public class SimpleCachingTargetRegistry implements TargetRegistry {

	private final Map cache = new ConcurrentHashMap();
	private final TargetRegistry delegate;
	
	public SimpleCachingTargetRegistry(TargetRegistry delegate) {
		super();
		this.delegate=delegate;
	}

	public Object getTarget(String context) {
		Object  target = cache.get(context);
		if (target == null) {
			target = delegate.getTarget(context);
			if (target != null) {
				cache.put(context, target);
			}
		}
		return target;
	}
}
