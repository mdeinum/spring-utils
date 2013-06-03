package biz.deinum.ws.transport;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.transport.WebServiceConnection;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Adapter which turns a {@code WebServiceConnection} into a fire-and-forget connection.
 *
 * @author Marten Deinum
 */
public class FireAndForgetWebServiceConnectionAdapter implements WebServiceConnection {

    private final WebServiceConnection delegate;

    public FireAndForgetWebServiceConnectionAdapter(WebServiceConnection delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public String getErrorMessage() throws IOException {
        return delegate.getErrorMessage();
    }

    @Override
    public URI getUri() throws URISyntaxException {
        return delegate.getUri();
    }

    @Override
    public boolean hasError() throws IOException {
        return delegate.hasError();
    }

    @Override
    public WebServiceMessage receive(WebServiceMessageFactory messageFactory) throws IOException {
        return null;
    }

    @Override
    public void send(WebServiceMessage message) throws IOException {
        delegate.send(message);
    }
}
