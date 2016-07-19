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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ws.transport.WebServiceMessageSender;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: in329dei
 * Date: 3-6-13
 * Time: 10:16
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class FireAndForgetWebServiceMessageSenderAdapterTest {

    @Mock
    private WebServiceMessageSender delegate;

    private FireAndForgetWebServiceMessageSenderAdapter adapter;

    public void before() {
        adapter = new FireAndForgetWebServiceMessageSenderAdapter(delegate);
    }

    @Test
    public void methodDelegation() throws Exception {
        delegate.createConnection(null);
        delegate.supports(null);

        verify(delegate, times(1)).createConnection(null);
        verify(delegate, times(1)).supports(null);

    }


}
