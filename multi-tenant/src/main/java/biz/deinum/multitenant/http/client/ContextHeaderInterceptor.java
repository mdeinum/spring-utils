package biz.deinum.multitenant.http.client;

import biz.deinum.multitenant.core.ContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * {@code ClientHttpRequestInterceptor} which reads and writes the context to a header in the request.
 *
 * @author Marten Deinum
 * @since 1.3.0
 */
public class ContextHeaderInterceptor implements ClientHttpRequestInterceptor {

    private static final String DEFAULT_HEADER_NAME = ContextHeaderInterceptor.class.getName() + ".CONTEXT";

    private String headerName = DEFAULT_HEADER_NAME;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        String context = ContextHolder.getContext();
        if (StringUtils.hasText(context)) {
            request.getHeaders().add(this.headerName, context);
        }
        return execution.execute(request, body);
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
