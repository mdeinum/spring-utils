package biz.deinum.ws.transport;

import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.io.IOException;
import java.net.URI;

/**
 * @author Marten Deinum
 */
public class FireAndForgetWebServiceMessageSenderAdapter implements WebServiceMessageSender {

    private final WebServiceMessageSender delegate;


    public FireAndForgetWebServiceMessageSenderAdapter(WebServiceMessageSender delegate) {
        this.delegate = delegate;
    }

    public WebServiceConnection createConnection(URI uri) throws IOException {
        return new FireAndForgetWebServiceConnectionAdapter(delegate.createConnection(uri));
    }

    public boolean supports(URI uri) {
        return delegate.supports(uri);
    }
}
