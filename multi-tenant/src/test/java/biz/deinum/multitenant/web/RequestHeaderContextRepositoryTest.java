package biz.deinum.multitenant.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

/**
 * @author marten
 */
public class RequestHeaderContextRepositoryTest {

    private RequestHeaderContextRepository repository;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void whenRequestParameterFoundThenValueShouldBeReturned() {
        repository = new RequestHeaderContextRepository("context", "default-test-value");
        request.addHeader("context", "test");
        String context = repository.getContext(request, response);
        assertEquals("test", context);
    }

    @Test
    public void whenRequestParameterNotFoundThenDefaultValueShouldBeReturned() {
        repository = new RequestHeaderContextRepository("context", "default-test-value");
        String context = repository.getContext(request, response);
        assertEquals("default-test-value", context);
    }

}