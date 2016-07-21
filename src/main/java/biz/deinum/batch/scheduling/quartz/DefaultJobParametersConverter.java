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
import java.util.Map;

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

        JobParametersBuilder builder = new JobParametersBuilder();
        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            boolean identifying = isIdentifyingKey(key);
            if (!identifying) {
                key = key.replaceFirst(NON_IDENTIFYING_FLAG, "");
            } else if (identifying && key.startsWith(IDENTIFYING_FLAG)) {
                key = key.replaceFirst("\\" + IDENTIFYING_FLAG, "");
            }

            if (value instanceof String) {
                builder.addString(key, (String) value, identifying);
            } else if (value instanceof Long) {
                builder.addLong(key, (Long) value, identifying);
            } else if (value instanceof Double) {
                builder.addDouble(key, (Double) value, identifying);
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value, identifying);
            } else if (value instanceof JobParameter) {
                builder.addParameter(key, (JobParameter) value);
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
        JobDataMap jobDataMap = new JobDataMap();

        if (params != null && !params.isEmpty()) {
            Map<String, JobParameter> parameters = params.getParameters();
            for (Map.Entry<String, JobParameter> entry : parameters.entrySet()) {

                String key = entry.getKey();
                JobParameter jobParameter = entry.getValue();
                Object value = jobParameter.getValue();
                if (value != null) {
                    key = (!jobParameter.isIdentifying() ? NON_IDENTIFYING_FLAG : "") + key;
                    jobDataMap.put(key, value);
                }
            }
        }
        return jobDataMap;
    }
}
