package biz.deinum.multitenant.web;

import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Basic implementation of the {@code ContextRepository} uses a request parameter to determine the context to use.
 * This is not the best implementation nor the wisest it merely servers as an example!
 *
 *
 * @author Marten Deinum
 */
public class RequestParameterContextRepository implements ContextRepository {

    private static final String DEFAULT_REQUEST_PARAMETER = "context";

    private final String parameter;
    private final String defaultContext;

    public RequestParameterContextRepository(String defaultContext) {
        this(DEFAULT_REQUEST_PARAMETER, defaultContext);
    }

    public RequestParameterContextRepository(String parameter, String defaultContext) {
        super();
        this.parameter=parameter;
        this.defaultContext=defaultContext;
    }


    @Override
    public String getContext(HttpServletRequest request, HttpServletResponse response) {
        return ServletRequestUtils.getStringParameter(request, parameter, defaultContext);
    }
}
