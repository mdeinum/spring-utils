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
