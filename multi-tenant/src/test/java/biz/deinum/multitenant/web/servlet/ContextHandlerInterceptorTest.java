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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Tests for the ContextInterceptor.
 *
 * @author Marten Deinum
 */
@RunWith(MockitoJUnitRunner.class)
public class ContextHandlerInterceptorTest {

    @Mock
    private ContextRepository repository;

    private HttpServletRequest request = new  MockHttpServletRequest();
    private HttpServletResponse response = new MockHttpServletResponse();

    private ContextHandlerInterceptor interceptor;

    @Before
    public void before() {
        ContextHolder.clear();
        interceptor = new ContextHandlerInterceptor(repository);
    }

    @Test
    public void whenContextFoundThenTheContextShouldBeSet() throws Exception {
        when(repository.getContext(request, response)).thenReturn("test");
        interceptor.preHandle(request, response, null);
        assertThat(ContextHolder.getContext(), is("test"));
    }

    @Test(expected = IllegalStateException.class)
    public void whenNoContextFoundThenAnExceptionShouldBeThrown() throws Exception {
        when(repository.getContext(request, response)).thenReturn(null);
        interceptor.preHandle(request, response, null);
    }

    @Test
    public void whenNoContextFoundThenContextShouldBeNull() throws Exception {
        interceptor.setThrowExceptionOnMissingContext(false);
        when(repository.getContext(request, response)).thenReturn(null);
        interceptor.preHandle(request, response, null);
        assertNull(ContextHolder.getContext());
    }

    @Test
    public void whenAfterCompletionIsCalledThenTheContextShouldBeNull() throws Exception {
        when(repository.getContext(request, response)).thenReturn("test");
        interceptor.preHandle(request, response, null);
        assertThat(ContextHolder.getContext(), is("test"));
        interceptor.afterCompletion(request, response, null, null);
        assertNull(ContextHolder.getContext());
    }


}
