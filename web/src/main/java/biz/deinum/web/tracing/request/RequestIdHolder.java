package biz.deinum.web.tracing.request;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 16-9-13
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public abstract class RequestIdHolder {

    private static final ThreadLocal<String> holder = new ThreadLocal<String>();

    public String getRequestId() {
        return holder.get();
    }

    static void setRequestId(String id) {
        holder.set(id);
    }

    static void clear() {
        holder.remove();
    }
}
