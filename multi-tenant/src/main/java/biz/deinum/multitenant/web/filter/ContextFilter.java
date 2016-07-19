/*
 *  Copyright 2007-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package biz.deinum.multitenant.web.filter;

import biz.deinum.multitenant.core.ContextHolder;
import biz.deinum.multitenant.web.ContextRepository;
import biz.deinum.multitenant.web.servlet.ContextHandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@code javax.servlet.Filter} which sets the context from the current request.
 *
 *  Delegates the actual lookup to a {@code ContextRepository}.
 *
 * When no context is found an IllegalStateException is thrown, this can be
 * switched of by setting the {@code throwExceptionOnMissingContext}property.
 *
 * @author Marten Deinum
 * @since 1.3
 * @see ContextHandlerInterceptor
 */
public class ContextFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(ContextFilter.class);

    private final ContextRepository contextRepository;

    private boolean throwExceptionOnMissingContext = true;

    public ContextFilter(ContextRepository contextRepository) {
        super();
        this.contextRepository = contextRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String context = contextRepository.getContext(request, response);
            logger.debug("Using context: {}", context);
            if (throwExceptionOnMissingContext && !StringUtils.hasText(context)) {
                throw new IllegalStateException("Could not determine context for current request!");
            } else {
                ContextHolder.setContext(context);
                filterChain.doFilter(request, response);
            }
        } finally {
            //Always clear the thread local after request processing.
            ContextHolder.clear();
        }
    }

    /**
     * When <code>true</code> (the default) an exception is throw if no context is found for the current request.
     *
     * @param throwExceptionOnMissingContext
     */
    public void setThrowExceptionOnMissingContext(boolean throwExceptionOnMissingContext) {
        this.throwExceptionOnMissingContext = throwExceptionOnMissingContext;
    }
}
