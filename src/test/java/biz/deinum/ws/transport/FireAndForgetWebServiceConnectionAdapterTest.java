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

package biz.deinum.ws.transport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 3-6-13
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class FireAndForgetWebServiceConnectionAdapterTest {



    @Mock
    private WebServiceConnection delegate;

    @Mock
    private WebServiceMessage message;

    private FireAndForgetWebServiceConnectionAdapter adapter;

    @Before
    public void before() {
        adapter = new FireAndForgetWebServiceConnectionAdapter(delegate);
    }

    @Test
    public void methodDelegation() throws Exception {
        adapter.close();
        adapter.getErrorMessage();
        adapter.getUri();
        adapter.hasError();
        adapter.send(message);

        assertNull(adapter.receive(null));

        verify(delegate, times(1)).close();
        verify(delegate, times(1)).getErrorMessage();
        verify(delegate, times(1)).getUri();
        verify(delegate, times(1)).hasError();
        verify(delegate, times(1)).send(message);
        verify(delegate, never()).receive(any(WebServiceMessageFactory.class));

    }


}
