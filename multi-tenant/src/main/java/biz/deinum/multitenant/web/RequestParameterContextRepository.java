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
