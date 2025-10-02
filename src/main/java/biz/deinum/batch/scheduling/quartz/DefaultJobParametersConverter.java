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

package biz.deinum.batch.scheduling.quartz;

import java.util.Date;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

/**
 * Converter for {@link JobParameters} instances using a simple naming
 * convention for property keys. Key names that are prefixed with a - are
 * considered non-identifying and will not contribute to the identity of a
 * {@link JobInstance}.
 *
 * @author Marten Deinum
 * @see org.springframework.batch.core.converter.DefaultJobParametersConverter
 */
public class DefaultJobParametersConverter implements JobParametersConverter {

    private static final Logger logger = LoggerFactory.getLogger(DefaultJobParametersConverter.class);

    private static final String NON_IDENTIFYING_FLAG = "-";
    private static final String IDENTIFYING_FLAG = "+";

    /**
     * Check for suffix on keys and use those to decide how to convert the
     * value. It ignores types that aren't supported as {@code JobParameter} a message
     * for the skipped properties will be logged.
     *
     * @see org.springframework.batch.core.converter.JobParametersConverter#getJobParameters(java.util.Properties)
     */
    @Override
    public JobParameters getJobParameters(JobDataMap jobDataMap) {
        if (jobDataMap == null || jobDataMap.isEmpty()) {
            return new JobParameters();
        }

        var builder = new JobParametersBuilder();
        for (var entry : jobDataMap.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var identifying = isIdentifyingKey(key);
            if (!identifying) {
                key = key.replaceFirst(NON_IDENTIFYING_FLAG, "");
            } else if (identifying && key.startsWith(IDENTIFYING_FLAG)) {
                key = key.replaceFirst("\\" + IDENTIFYING_FLAG, "");
            }

            if (value instanceof String val) {
                builder.addString(key, val, identifying);
            } else if (value instanceof Long val) {
                builder.addLong(key, val, identifying);
            } else if (value instanceof Double val) {
                builder.addDouble(key, val, identifying);
            } else if (value instanceof Date val) {
                builder.addDate(key, val, identifying);
            } else if (value instanceof JobParameter<?> val) {
                builder.addJobParameter(key, val);
            } else {
                logger.debug("Ignoring context property '{}' with value '{}' not a supported type.", key, value);
            }
        }
        return builder.toJobParameters();
    }

    private boolean isIdentifyingKey(String key) {
        boolean identifying = true;

        if (key.startsWith(NON_IDENTIFYING_FLAG)) {
            identifying = false;
        }

        return identifying;
    }


    @Override
    public JobDataMap getJobDataMap(JobParameters params) {
        var jobDataMap = new JobDataMap();

        if (params != null && !params.isEmpty()) {
            var parameters = params.getParameters();
            for (var entry : parameters.entrySet()) {

                var key = entry.getKey();
                var jobParameter = entry.getValue();
                var value = jobParameter.getValue();
                key = (!jobParameter.isIdentifying() ? NON_IDENTIFYING_FLAG : "") + key;
                jobDataMap.put(key, value);
            }
        }
        return jobDataMap;
    }
}
