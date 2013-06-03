package biz.deinum.multitenant.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Repository for loading the context based on the current request and/or response.
 *
 * @author Marten Deinum
 */
public interface ContextRepository {

    String getContext(HttpServletRequest request, HttpServletResponse response);
}
