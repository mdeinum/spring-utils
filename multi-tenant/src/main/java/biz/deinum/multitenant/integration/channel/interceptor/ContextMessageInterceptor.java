package biz.deinum.multitenant.integration.channel.interceptor;

import biz.deinum.multitenant.core.ContextHolder;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.channel.interceptor.ChannelInterceptorAdapter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.StringUtils;

/**
 * {@code ChannelInterceptor} which reads and writes the context to a header in the message.
 *
 * @author Marten Deinum
 * @since 1.3.0
 */
public class ContextMessageInterceptor extends ChannelInterceptorAdapter {

    private static final String DEFAULT_HEADER_NAME = ContextMessageInterceptor.class.getName() + ".CONTEXT";

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
            builder = builder.setHeader(headerName, context);
            return builder.build();
        }
        return message;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel messageChannel) {
        String context = message.getHeaders().get(headerName, String.class);
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
