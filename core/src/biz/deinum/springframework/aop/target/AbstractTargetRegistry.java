package biz.deinum.springframework.aop.target;

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
	 * Retrieve the target.
	 * 
	 * @param context
	 * @return
	 * @see #getTarget(String)
	 */
	protected abstract Object getTargetInternal(final String context); 

	
	
}
