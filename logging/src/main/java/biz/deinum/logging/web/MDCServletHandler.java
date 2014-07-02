package biz.deinum.logging.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by in329dei on 13-2-14.
 */
public interface MDCServletHandler {

    void handle(ServletRequest request, ServletResponse response);
    void clean();
}
