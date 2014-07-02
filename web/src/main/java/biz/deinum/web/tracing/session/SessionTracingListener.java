package biz.deinum.web.tracing.session;

import org.slf4j.MDC;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 16-9-13
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class SessionTracingListener implements ServletRequestListener, HttpSessionListener {

    private static final String ATTRIBUTE_NAME = "SESSION_TRACKING_ID";

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        String id= "N/A";
        if (sre.getServletRequest() instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) sre.getServletRequest()).getSession(false);
            if (session != null) {
                id = session.getId();
            }
        }
        sre.getServletRequest().setAttribute(ATTRIBUTE_NAME, id);
        MDC.put(ATTRIBUTE_NAME, id);
        SessionIdHolder.setSessionId(id);
    }


    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        MDC.remove(ATTRIBUTE_NAME);
        SessionIdHolder.clear();
    }


    /**
     * When Session is created after the request is already handled, set at least the id in the holder and MDC.
     *
     * @param se
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (SessionIdHolder.getSessionId() == null || "N/A".equals(SessionIdHolder.getSessionId())) {
            String id = se.getSession().getId();
            MDC.put(ATTRIBUTE_NAME, id);
            SessionIdHolder.setSessionId(id);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }
}
