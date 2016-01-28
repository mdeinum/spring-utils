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