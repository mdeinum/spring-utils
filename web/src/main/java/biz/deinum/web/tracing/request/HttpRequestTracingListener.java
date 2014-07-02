package biz.deinum.web.tracing.request;

import biz.deinum.web.tracing.IdRepository;
import biz.deinum.web.tracing.StatelessIdRepository;
import org.slf4j.MDC;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * {@code ServletRequestListener} which creates a unique id for each incoming request. This id is stored in multiple locations
 * - as a request attribute
 * - as a thread local
 * - in the MDC from SLF4J (so that it can be used in logfiles)
 *
 * @author Marten Deinum
 */
public class HttpRequestTracingListener implements ServletRequestListener {

    private static final String ATTRIBUTE_NAME = "REQUEST_TRACKING_ID";

    private IdRepository idRepository = new StatelessIdRepository();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        String id = idRepository.createOrGet(sre.getServletRequest());
        sre.getServletRequest().setAttribute(ATTRIBUTE_NAME, id);
        MDC.put(ATTRIBUTE_NAME, id);
        RequestIdHolder.setRequestId(id);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        MDC.remove(ATTRIBUTE_NAME);
        RequestIdHolder.clear();
    }


}
