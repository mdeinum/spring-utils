package biz.deinum.multitenant.integration.channel.interceptor;

import biz.deinum.multitenant.core.ContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.Message;
import org.springframework.integration.message.GenericMessage;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 4-6-13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
public class ContextMessageInterceptorTest {

    private Message message;

    private ContextMessageInterceptor interceptor = new ContextMessageInterceptor();


    @Before
    public void before() {
        message = new GenericMessage("dummy-test-payload");
    }
    @After
    public void after() {
        ContextHolder.clear();
    }

    @Test
    public void whenContextIsSetThenTheContextHeaderShouldBeSet() {
        Message msg = interceptor.preSend(message, null);
        String context = msg.getHeaders().get(interceptor.getHeaderName(), String.class);
        assertNull(context);
    }

    @Test
    public void whenNoContextIsSetThenNoContextHeaderShouldBeSet() {
        ContextHolder.setContext("test");
        Message msg = interceptor.preSend(message, null);
        String context = msg.getHeaders().get(interceptor.getHeaderName(), String.class);
        assertEquals("test", context);
    }

    @Test
    public void whenContextHeaderIsSetThenContextShouldBeSet() {
        message = new GenericMessage("dummy-test-payload", Collections.singletonMap(interceptor.getHeaderName(), "test"));
        interceptor.postReceive(message, null);
        assertEquals("test", ContextHolder.getContext());
    }

    @Test
    public void whenNoContextHeaderIsSetThenContextShouldNotBeSet() {
        interceptor.postReceive(message, null);
        assertNull(ContextHolder.getContext());
    }


}
