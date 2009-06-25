package biz.deinum.springframework.aop.target.registry;

public interface TargetRegistry {

	/**
	 * Returns the Target object for the given context, or null when none can be found.
	 * 
	 * @param context
	 * @return
	 */
	public Object getTarget(final String context);

}
