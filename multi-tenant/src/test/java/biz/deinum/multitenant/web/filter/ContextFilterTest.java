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

package biz.deinum.multitenant.web.filter;

import biz.deinum.multitenant.core.ContextHolder;
import biz.deinum.multitenant.web.ContextRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Tests for the ContextInterceptor.
 *
 * @author Marten Deinum
 */
@RunWith(MockitoJUnitRunner.class)
public class ContextFilterTest {

    @Mock
    private ContextRepository repository;

    private HttpServletRequest request = new MockHttpServletRequest();
    private HttpServletResponse response = new MockHttpServletResponse();
    private FilterChain mockChain = new MockFilterChain();

    private ContextFilter filter;

    @Before
    public void before() {
        ContextHolder.clear();
        filter = new ContextFilter(repository);
    }

    @Test
    public void whenContextFoundThenTheContextShouldBeSet() throws Exception {
        when(repository.getContext(request, response)).thenReturn("test");
        filter.doFilter(request, response, new VerifyFilterChain("test"));
    }

    @Test(expected = IllegalStateException.class)
    public void whenNoContextFoundThenAnExceptionShouldBeThrown() throws Exception {
        when(repository.getContext(request, response)).thenReturn(null);
        filter.doFilter(request, response, mockChain);
    }

    @Test
    public void whenNoContextFoundThenContextShouldBeNull() throws Exception {
        filter.setThrowExceptionOnMissingContext(false);
        when(repository.getContext(request, response)).thenReturn(null);
        filter.doFilter(request, response, new VerifyFilterChain(null));
    }

    @Test
    public void whenAfterCompletionIsCalledThenTheContextShouldBeNull() throws Exception {
        when(repository.getContext(request, response)).thenReturn("test");
        filter.doFilter(request, response, new VerifyFilterChain("test"));
        assertNull(ContextHolder.getContext());
    }

    private static class VerifyFilterChain implements FilterChain {

        private final String value;

        public VerifyFilterChain(String value) {
            super();
            this.value=value;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            assertEquals(value, ContextHolder.getContext());
        }
    }

}
