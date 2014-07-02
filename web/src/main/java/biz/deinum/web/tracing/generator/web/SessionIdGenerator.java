package biz.deinum.web.tracing.generator.web;

import biz.deinum.web.tracing.IdGenerator;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 6-12-13
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class SessionIdGenerator implements IdGenerator {
    @Override
    public String generate(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getSession().getId();
        }
        throw new IllegalArgumentException("Can only create Session for HttpServletRequest, not for " + request.getClass());
    }
}
