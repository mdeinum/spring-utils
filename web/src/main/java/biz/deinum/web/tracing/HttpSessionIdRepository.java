package biz.deinum.web.tracing;

import biz.deinum.web.tracing.generator.UUIDIdGenerator;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 16-9-13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
public class HttpSessionIdRepository implements IdRepository {

    private static final String DEFAULT_ID_PARAMETER = "TRACKING_ID";

    private IdGenerator generator = new UUIDIdGenerator();

    @Override
    public String createOrGet(ServletRequest request) {
        String id = null;
        if (request instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) request).getSession();
            id = (String) session.getAttribute(DEFAULT_ID_PARAMETER);
            if (id == null) {
                id = generator.generate(request);
                session.setAttribute(DEFAULT_ID_PARAMETER, id);
            }
            return id;
        }
        throw new IllegalArgumentException("Only supported for HttpServletRequest instances, not for " + request.getClass());
    }

}
