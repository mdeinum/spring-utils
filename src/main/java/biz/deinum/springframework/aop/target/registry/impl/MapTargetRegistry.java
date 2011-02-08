package biz.deinum.springframework.aop.target.registry.impl;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import biz.deinum.springframework.aop.target.registry.AbstractTargetRegistry;

/**
 * TargetRegistry which retrieves the correct target from a Map.
 * 
 * @author Marten Deinum
 * @version 1.0
 * 
 */
public class MapTargetRegistry<T> extends AbstractTargetRegistry<T> {

	private final Map<String, T> targets = Collections.synchronizedMap(new WeakHashMap<String, T>());
	
	public void setTargets(final Map<String, T> targets) {
		this.targets.clear();
		this.targets.putAll(targets);
	}

	/**
	 * Retrieves the target from the configured Map. Using the context as a key.
	 * 
	 * @return the target or <code>null</code>
	 */
	protected T getTargetInternal(String context) {
		return targets.get(context);	
	}

}
