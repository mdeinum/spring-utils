/*
 * Copyright 2007-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package biz.deinum.batch.scheduling.quartz;

import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.util.Assert;

/**
 * Simple Quartz {@code Job} for preparing and launcing a Spring Batch{@code Job}.
 * It requires a {@code JobLauncher} and {@code JobLocator} to be configured.
 *
 * The {@code jobName} can be injected from the {@code JobDetails} as well as the other
 * required properties.
 *
 * @author Marten Deinum
 */
public class LaunchingJob implements Job {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    private String jobName;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Assert.notNull(jobLauncher, "A JobLauncher is required.");
        Assert.notNull(jobLocator, "A JobLocator is required.");
        Assert.notNull(jobName, "A JobName is required.");

        try {
            org.springframework.batch.core.Job jobToLaunch = findJob(this.jobName);
            this.jobLauncher.run(jobToLaunch, getJobParameters(context));
        } catch (org.springframework.batch.core.JobExecutionException e) {
            throw new JobExecutionException("Error executing job '"+this.jobName +"'.", e);
        }
    }

    protected JobParameters getJobParameters(JobExecutionContext context) {
        final JobDataMap jobDataMap = context.getMergedJobDataMap();
        final JobParametersBuilder builder = new JobParametersBuilder();
        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                builder.addString(key, (String) value);
            } else if (value instanceof Long) {
                builder.addLong(key, (Long) value);
            } else if (value instanceof Double) {
                builder.addDouble(key, (Double) value);
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            } else if (value instanceof JobParameter) {
                builder.addParameter(key, (JobParameter) value);
            } else {
                logger.debug("Ignoring context property '{}' with value '{}' not a supported type.", key, value);
            }
        }
        return builder.toJobParameters();
    }

    protected org.springframework.batch.core.Job findJob(String jobName) throws NoSuchJobException {
        return this.jobLocator.getJob(jobName);
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
