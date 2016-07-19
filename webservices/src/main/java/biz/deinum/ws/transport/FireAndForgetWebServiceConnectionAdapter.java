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
