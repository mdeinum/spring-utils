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

package biz.deinum.web.loging;

import org.slf4j.MDC;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ServletFilter which exposes certain variables into the MDC.
 *
 * @author Marten Deinum
 */
public class MDCInsertingServletFilter extends OncePerRequestFilter {

    public static final String REQUEST_REMOTE_HOST_MDC_KEY = "req.remoteHost";
    public static final String REQUEST_USER_AGENT_MDC_KEY = "req.userAgent";
    public static final String REQUEST_REQUEST_URI = "req.requestURI";
    public static final String REQUEST_QUERY_STRING = "req.queryString";
    public static final String REQUEST_REQUEST_URL = "req.requestURL";
    public static final String REQUEST_X_FORWARDED_FOR = "req.xForwardedFor";
    public static final String REQUEST_SESSION_ID = "req.sessionId";
    public static final String HOSTNAME = "hostname";

    private String host = "unknown";

    @Override
    protected void initFilterBean() throws ServletException {
        try {
            WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
            InetAddress local = InetAddress.getLocalHost();
            host = local.getHostName();
        } catch (UnknownHostException e) {
            logger.info("Could not retrieve hostname.", e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        insertIntoMDC(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            clearMDC();
        }

    }

    void insertIntoMDC(HttpServletRequest request) {
        MDC.put(HOSTNAME, host);
        MDC.put(REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());
        MDC.put(REQUEST_REQUEST_URI, request.getRequestURI());
        StringBuffer requestURL = request.getRequestURL();
        if (requestURL != null) {
            MDC.put(REQUEST_REQUEST_URL, requestURL.toString());
        }
        MDC.put(REQUEST_QUERY_STRING, request.getQueryString());
        MDC.put(REQUEST_USER_AGENT_MDC_KEY, request.getHeader("User-Agent"));
        MDC.put(REQUEST_X_FORWARDED_FOR, request.getHeader("X-Forwarded-For"));

        MDC.put(REQUEST_SESSION_ID, WebUtils.getSessionId(request));

    }

    void clearMDC() {
        MDC.remove(REQUEST_REMOTE_HOST_MDC_KEY);
        MDC.remove(REQUEST_REQUEST_URI);
        MDC.remove(REQUEST_QUERY_STRING);
        // removing possibly inexistent item is OK
        MDC.remove(REQUEST_REQUEST_URL);
        MDC.remove(REQUEST_USER_AGENT_MDC_KEY);
        MDC.remove(REQUEST_X_FORWARDED_FOR);
        MDC.remove(REQUEST_SESSION_ID);
        MDC.remove(HOSTNAME);

    }
}
