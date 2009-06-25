package biz.deinum.springframework.aop.target.registry;

/**
 * Implementors of this interface have the opportunity to modify some final
 * settings on the target.
 *  
 * @author Marten Deinum
 *
 * @see biz.deinum.springframework.aop.target.ContextSwappableTargetSource
 *
 */
public interface TargetPostProcessor {

	/**
	 * true if this <em>TargetPostProcessor</em> supports this type of target.
	 * 
	 * @param class1
	 * @return
	 */
	boolean supports(Class class1);

	/**
	 * Do some post processing on the given <code>target</code>.
	 * @param target
	 */
	void postProcess(Object target);

}
