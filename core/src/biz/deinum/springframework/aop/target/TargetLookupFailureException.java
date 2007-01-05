package biz.deinum.springframework.aop.target;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception to be thrown by the {@link ContextSwappableTargetSource} indicating that no
 * target could be found.
 * 
 * @author Marten Deinum
 * @version 1.0
 */
public class TargetLookupFailureException extends NestedRuntimeException {

	public TargetLookupFailureException(String message) {
		super(message);
	}
	
	public TargetLookupFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
