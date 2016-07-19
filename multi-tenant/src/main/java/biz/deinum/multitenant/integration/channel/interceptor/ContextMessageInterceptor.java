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

package biz.deinum.multitenant.integration.channel.interceptor;

import biz.deinum.multitenant.core.ContextHolder;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.util.StringUtils;

/**
 * {@code ChannelInterceptor} which reads and writes the context to a header in the message.
 *
 * @author Marten Deinum
 * @since 1.3.0
 */
public class ContextMessageInterceptor extends ChannelInterceptorAdapter {

    private static final String DEFAULT_HEADER_NAME = ContextHolder.class.getName() + ".CONTEXT";

    private String headerName = DEFAULT_HEADER_NAME;

    /**
     * Set the current context on the message so that further down the chain it can be set again.
     *
     * @param message
     * @param messageChannel
     * @return the message with the context or the original if no header was set.
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
        String context = ContextHolder.getContext();
        if (StringUtils.hasText(context)) {
            MessageBuilder<?> builder = MessageBuilder.fromMessage(message);
            builder = builder.setHeader(this.headerName, context);
            return builder.build();
        }
        return message;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel messageChannel) {
        String context = message.getHeaders().get(this.headerName, String.class);
        if (StringUtils.hasText(context)) {
            ContextHolder.setContext(context);
        }
        return message;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    protected String getHeaderName() {
        return this.headerName;
    }
}
