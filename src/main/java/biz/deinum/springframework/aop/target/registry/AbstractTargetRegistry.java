package biz.deinum.springframework.aop.target.registry;


/**
 * Generic implementation and functionality for a {@link TargetRegistry}. 
 * This class provides optional chaining of TargetRestry instances.
 * 
 * @author Marten Deinum
 * @since 1.1
 */
public abstract class AbstractTargetRegistry implements TargetRegistry {

	private TargetRegistry parent = null;
	
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
	
	/**
	 * Retrieve the target. Subclasses must implement this method.
	 * 
	 * @param context
	 * @return
	 * @see #getTarget(String)
	 */
	protected abstract Object getTargetInternal(final String context); 

	
	
}
