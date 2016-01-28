package biz.deinum.multitenant.web;

import biz.deinum.multitenant.core.ContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Use a request header to determine the context.
 *
 * @author Marten Deinum
 * @since 1.3
 */
public class RequestHeaderContextRepository implements ContextRepository {

    private static final String DEFAULT_HEADER_NAME = ContextHolder.class.getName() + ".CONTEXT";

    private final String headerName;
    private final String defaultContext;

    public RequestHeaderContextRepository(String defaultContext) {
        this(DEFAULT_HEADER_NAME, defaultContext);
    }

    public RequestHeaderContextRepository(String headerName, String defaultContext) {
        this.headerName = headerName;
        this.defaultContext = defaultContext;
    }

    @Override
    public String getContext(HttpServletRequest request, HttpServletResponse response) {
        String context = request.getHeader(headerName);
        if (StringUtils.hasText(context)) {
            return context;
        }
        return this.defaultContext;
    }

}
