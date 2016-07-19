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
 * {@code HandlerInterceptor} which sets the context from the current request.
 * Delegates the actual lookup to a {@code ContextRepository}.
 *
 * When no context is found an IllegalStateException is thrown, this can be
 * switched of by setting the {@code throwExceptionOnMissingContext} property.
 *
 * @author Marten Deinum
 * @since 1.3
 * @see biz.deinum.multitenant.web.filter.ContextFilter
 */
public class ContextHandlerInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(ContextHandlerInterceptor.class);
    private final ContextRepository contextRepository;

    private boolean throwExceptionOnMissingContext = true;

    public ContextHandlerInterceptor(ContextRepository contextRepository) {
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
