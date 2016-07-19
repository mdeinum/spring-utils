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

package biz.deinum.multitenant.http.client;

import biz.deinum.multitenant.core.ContextHolder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

/**
 * @author marten
 */
public class ContextHeaderInterceptorTest  {

    private ContextHeaderInterceptor interceptor = new ContextHeaderInterceptor();

    @Test
    public void shouldSetHeaderOnRequest() {
        interceptor.setHeaderName("context");

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(interceptor);

        RestTemplate template = new RestTemplate();
        template.setInterceptors(interceptors);

        MockRestServiceServer mockServer = MockRestServiceServer.createServer(template);

        mockServer.expect(requestTo("/test"))
                .andExpect(method(GET))
                .andExpect(header("context", "test-context"))
                .andRespond(MockRestResponseCreators.withSuccess());

        ContextHolder.setContext("test-context");

        HttpEntity entity = new HttpEntity(null, null);

        template.exchange("/test", GET, entity, String.class);

        mockServer.verify();

    }



}