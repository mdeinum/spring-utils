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

import jakarta.annotation.Nullable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.util.Assert;

/**
 * {@link ItemStreamReader} with a using a lock for syncronization. Is useful when needing to synchronize on read in a
 * concurrent environment.
 *
 * @author Marten Deinum
 */
public class SynchronizedItemStreamReader<T> implements ItemStreamReader<T> {

    private final ItemReader<T> delegate;
    private final Lock lock = new ReentrantLock();

    /**
     *
     * @param delegate delegate ItemReader which does the actual work.
     */
    public SynchronizedItemStreamReader(final ItemReader<T> delegate) {
      Assert.notNull(delegate, "The delegate must not be null");
      this.delegate = delegate;
    }

    @Override
    @Nullable
    public T read() throws Exception {
        this.lock.lock();
        try {
          return this.delegate.read();
        } finally {
          this.lock.unlock();
        }
    }

    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) this.delegate).open(executionContext);
        }
    }

    public void update(final ExecutionContext executionContext) throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) this.delegate).update(executionContext);
        }
    }

    public void close() throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) this.delegate).close();
        }
    }
}