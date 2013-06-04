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
