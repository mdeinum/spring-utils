package biz.deinum.springframework.core;

public abstract class ContextHolder {
	private static final ThreadLocal holder = new ThreadLocal();
	
	public static void setContext(String context) {
		holder.set(context);
	}
	
	public static String getContext() {
		return (String) holder.get();
	}
	
}
