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

package biz.deinum.multitenant.integration.channel.interceptor;

import biz.deinum.multitenant.core.ContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

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
        ContextHolder.clear();
        message = new GenericMessage("dummy-test-payload");
    }

    @After
    public void after() {
        ContextHolder.clear();
    }

    @Test
    public void whenNoContextIsSetThenNoContextHeaderShouldBeSet() {
        Message msg = interceptor.preSend(message, null);
        String context = msg.getHeaders().get(interceptor.getHeaderName(), String.class);
        assertNull(context);
    }

    @Test
    public void whenContextIsSetThenTheContextHeaderShouldBeSet() {
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
