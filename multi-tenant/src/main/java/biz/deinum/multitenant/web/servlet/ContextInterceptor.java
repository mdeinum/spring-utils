package biz.deinum.multitenant.web.servlet;

import biz.deinum.multitenant.core.ContextHolder;
import biz.deinum.multitenant.web.ContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@code HandlerInterceptor} which sets the context from the current request. Delegates the actual lookup to a
 * {@code ContextRepository}.
 *
 * When no context is found an IllegalStateException is thrown, this can be switched of by setting the
 * <code>throwExceptionOnMissingContext</code> property.
 *
 * @author Marten Deinum
 * @since 1.3
 * @see biz.deinum.multitenant.web.filter.ContextFilter
 */
public class ContextInterceptor extends HandlerInterceptorAdapter {
    private final Logger logger = LoggerFactory.getLogger(ContextInterceptor.class);
    private final ContextRepository contextRepository;
    private boolean throwExceptionOnMissingContext = true;

    public ContextInterceptor(ContextRepository contextRepository) {
        super();
        this.contextRepository = contextRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String context = contextRepository.getContext(request, response);
        logger.debug("Using context: {}", context);
        if (throwExceptionOnMissingContext && !StringUtils.hasText(context)) {
            throw new IllegalStateException("Could not determine context for current request!");
        } else {
            ContextHolder.setContext(context);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextHolder.clear();
    }

    public void setThrowExceptionOnMissingContext(boolean throwExceptionOnMissingContext) {
        this.throwExceptionOnMissingContext = throwExceptionOnMissingContext;
    }
}
