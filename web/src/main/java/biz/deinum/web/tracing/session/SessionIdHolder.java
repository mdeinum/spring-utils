package biz.deinum.web.tracing.session;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 16-9-13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class SessionIdHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<String>();


    public static String getSessionId() {
        return holder.get();
    }

    static void setSessionId(String id) {
        holder.set(id);
    }

    static void clear() {
        holder.remove();
    }
}
