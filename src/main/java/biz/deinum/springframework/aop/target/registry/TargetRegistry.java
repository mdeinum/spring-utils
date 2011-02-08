package biz.deinum.springframework.aop.target.registry;

public interface TargetRegistry<T> {

	/**
	 * Returns the Target object for the given context, or null when none can be found.
	 * 
	 * @param context
	 * @return
	 */
	public T getTarget(final String context);

}
