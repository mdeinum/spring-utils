package biz.deinum.springframework.core;

import org.slf4j.LoggerFactory;

/**
 * ContextHolder will hold a reference to a context which can be used through-out
 * the application. The reference will be stored in a <code>ThreadLocal</code>.
 * 
 * @author Marten Deinum
 * @version 1.0
 *
 */
public abstract class ContextHolder {

	private static final ThreadLocal holder = new ThreadLocal();
	
	public static void setContext(String context) {
		LoggerFactory.getLogger(ContextHolder.class).debug("context set '{}'", context);
		holder.set(context);
	}
	
	public static String getContext() {
		return (String) holder.get();
	}
	
}
