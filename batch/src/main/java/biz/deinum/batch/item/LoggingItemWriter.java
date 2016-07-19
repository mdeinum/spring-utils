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

package biz.deinum.batch.item;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

/**
 * {@link ItemWriter} implementation which simply logs the items to write. Can be useful for troubleshooting.
 *
 * @author Marten Deinum
 *
 * @param <T> the type to log.
 */
public class LoggingItemWriter<T> implements ItemWriter<T> {

    private final Logger logger = LoggerFactory.getLogger(LoggingItemWriter.class);

    public void write(final List<? extends T> items) throws Exception {
        for (final T item : items) {
            this.logger.trace("Writing item: {}", item);
        }
    }
}
