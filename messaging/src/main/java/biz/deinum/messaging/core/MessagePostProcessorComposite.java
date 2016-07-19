package biz.deinum.messaging.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.messaging.Message;
import org.springframework.messaging.core.MessagePostProcessor;

/**
 * Composite implementation of a {@link MessagePostProcessor}. Will iterate over the
 * configured collection of processors and execute them in order. Will take the given
 * ordering into account when configuring.
 *
 * @author Marten Deinum
 */
public class MessagePostProcessorComposite implements MessagePostProcessor {

    private final List<MessagePostProcessor> delegates = new ArrayList<>();

    public void addMessagePostProcessors(List<MessagePostProcessor> processors) {
        if (processors != null) {
            this.delegates.addAll(processors);
        }
        Collections.sort(this.delegates, AnnotationAwareOrderComparator.INSTANCE);
    }

    @Override
    public Message<?> postProcessMessage(Message<?> message) {
        Message<?> msg = message;
        for (MessagePostProcessor delegate : delegates) {
            msg = delegate.postProcessMessage(msg);
        }
        return msg;
    }
}
