package biz.deinum.multitenant.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 4-6-13
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */

public class RequestParameterContextRepositoryTest {

    private RequestParameterContextRepository repository;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void whenRequestParameterFoundThenValueShouldBeReturned() {
        repository = new RequestParameterContextRepository("default-test-value");
        request.setParameter("context", "test");
        String context = repository.getContext(request, response);
        assertEquals("test", context);
    }

    @Test
    public void whenRequestParameterNotFoundThenDefaultValueShouldBeReturned() {
        repository = new RequestParameterContextRepository("default-test-value");
        String context = repository.getContext(request, response);
        assertEquals("default-test-value", context);
    }

}
