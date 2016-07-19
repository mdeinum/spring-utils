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
