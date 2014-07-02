package biz.deinum.logging.web;

import biz.deinum.logging.MDCHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by in329dei on 5-2-14.
 */
public class MDCInsertingFilter extends GenericFilterBean {

    @Value("${APPLICATIECODE}")
    private String applicatieCode;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String server = request.getLocalAddr();
            int port = request.getLocalPort();
            String threadId = Thread.currentThread().getName();
            if (request instanceof HttpServletRequest) {
                String sessionId = WebUtils.getSessionId((HttpServletRequest) request);

            }

            // Add Server
            // Add Port
            // Add Application Code
            // Add Thread-Id
            // Add Audit ID + Audit Type
            handle(request, response);
            chain.doFilter(request, response);
        } finally {
            clean(request, response);
        }
    }

    private void handle(ServletRequest request, ServletResponse response) {
        for (int i = 0; i < handlers.size(); i++) {
            Object handler = handlers.get(i);
            if (handler instanceof MDCHandler) {
                ((MDCHandler) handler).handle();
            } else if (handler instanceof MDCServletHandler) {
                ((MDCServletHandler) handler).handle(request, response);
            } else {
                logger.warn("Cannot handle '" + handler.getClass() + "'");
            }
        }

    }
    private void clean(ServletRequest request, ServletResponse response) {
        for (int i = 0; i < handlers.size(); i++) {
            Object handler = handlers.get(i);
            if (handler instanceof MDCHandler) {
                ((MDCHandler) handler).clean();
            } else if (handler instanceof MDCServletHandler) {
                ((MDCServletHandler) handler).clean();
            } else {
                logger.warn("Cannot handle '" + handler.getClass() + "'");
            }
        }

    }
}
