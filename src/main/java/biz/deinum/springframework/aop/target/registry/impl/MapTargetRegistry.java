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
public class MapTargetRegistry extends AbstractTargetRegistry {

	private final Map targets = Collections.synchronizedMap(new WeakHashMap());
	
	public void setTargets(final Map targets) {
		this.targets.clear();
		this.targets.putAll(targets);
	}

	/**
	 * Retrieves the target from the configured Map. Using the context as a key.
	 * 
	 * @return the target or <code>null</code>
	 */
	protected Object getTargetInternal(String context) {
		return targets.get(context);	
	}

}
