package biz.deinum.springframework.aop.target.registry;

import org.springframework.core.Ordered;


/**
 * Generic implementation and functionality for a {@link TargetRegistry}. 
 * This class provides optional chaining of TargetRestry instances.
 * 
 * @author Marten Deinum
 * @since 1.1
 */
public abstract class AbstractTargetRegistry implements TargetRegistry, Ordered {

	private TargetRegistry parent = null;
	
	private int order;
	
	public final void setParentRegistry(final TargetRegistry registry) {
		this.parent=registry;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final Object getTarget(String context) {
		Object target = getTargetInternal(context);
		if (target == null && parent != null) {
			target = parent.getTarget(context);
		}
		return target;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order=order;
	}
	
	/**
	 * Retrieve the target. Subclasses must implement this method.
	 * 
	 * @param context
	 * @return
	 * @see #getTarget(String)
	 */
	protected abstract Object getTargetInternal(final String context); 

	
	
}
