package biz.deinum.springframework.aop.target;

import org.springframework.core.NestedRuntimeException;

/**
 * Exception to be thrown by the {@link ContextSwappableTargetSource} indicating that no
 * target could be found.
 * 
 * @author Marten Deinum
 * @version 1.0.0
 */
public class TargetLookupFailureException extends NestedRuntimeException {

	private static final long serialVersionUID = -1645374642832222666L;

	public TargetLookupFailureException(String message) {
		super(message);
	}
	
	public TargetLookupFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
