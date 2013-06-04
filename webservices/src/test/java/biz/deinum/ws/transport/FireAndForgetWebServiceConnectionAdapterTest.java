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
