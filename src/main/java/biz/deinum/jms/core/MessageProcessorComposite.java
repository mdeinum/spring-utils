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

package biz.deinum.jms.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * Composite implementation of a {@link MessagePostProcessor}. Will iterate over the
 * configured collection of processors and execute them in order. Will take the given
 * ordering into account when configuring.
 *
 * @author Marten Deinum
 */
public class MessageProcessorComposite implements MessagePostProcessor {

    private final List<MessagePostProcessor> delegates = new ArrayList<>();

    public void addMessagePostProcessors(List<MessagePostProcessor> processors) {
        if (processors != null) {
            this.delegates.addAll(processors);
        }
        Collections.sort(this.delegates, AnnotationAwareOrderComparator.INSTANCE);
    }

    @Override
    public Message postProcessMessage(Message message) throws JMSException {
        Message msg = message;
        for (MessagePostProcessor delegate : delegates) {
            msg = delegate.postProcessMessage(msg);
        }
        return msg;
    }
}
