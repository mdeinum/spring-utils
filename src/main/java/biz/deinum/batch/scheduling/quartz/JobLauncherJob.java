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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Simple Quartz {@code Job} for preparing and launcing a Spring Batch {@code Job}.
 * It requires a {@code JobLauncher} and {@code JobLocator} to be configured.
 * <p>
 * The {@code jobName} can be injected from the {@code JobDetails} as well as the other
 * required properties.
 * <p>
 * Great parts (and improvements) have been inspired by the {@code JobLauncherCommandLineRunner}
 * from the Spring Boot Batch integration package.
 *
 * @author Marten Deinum
 */

public class JobLauncherJob implements Job {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private JobParametersConverter converter = new DefaultJobParametersConverter();

    private JobLauncher jobLauncher;
    private JobRegistry jobRegistry;
    private JobExplorer jobExplorer;

    private String jobName;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Assert.notNull(this.jobLauncher, "A JobLauncher is required.");
            Assert.notNull(this.jobRegistry, "A JobRegistry is required.");
            Assert.notNull(this.jobExplorer, "A JobExplorer is required.");
            Assert.notNull(this.jobName, "A JobName is required.");
        } catch (IllegalArgumentException iae) {
            throw new JobExecutionException(iae.getMessage(), iae);
        }

        try {
            org.springframework.batch.core.Job jobToLaunch = findJob(this.jobName);
            JobParameters jobParameters = getJobParameters(context);
            JobExecution result = execute(jobToLaunch, jobParameters);
            context.setResult(result);

        } catch (org.springframework.batch.core.JobExecutionException e) {
            throw new JobExecutionException("Error executing job '" + this.jobName + "'.", e);
        }
    }

    protected JobExecution execute(org.springframework.batch.core.Job job, JobParameters jobParameters) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters nextParameters = getNextJobParameters(job, jobParameters);
        logger.info("Using parameters: {}", nextParameters);
        if (nextParameters != null) {
            JobExecution execution = this.jobLauncher.run(job, nextParameters);
            return execution;
        }
        return null;
    }

    private JobParameters getNextJobParameters(org.springframework.batch.core.Job job,
                                               JobParameters additionalParameters) {
        String name = job.getName();
        JobParameters parameters = new JobParameters();
        List<JobInstance> lastInstances = this.jobExplorer.getJobInstances(name, 0, 1);
        JobParametersIncrementer incrementer = job.getJobParametersIncrementer();
        Map<String, JobParameter> additionals = additionalParameters.getParameters();
        if (lastInstances.isEmpty()) {
            // Start from a completely clean sheet
            if (incrementer != null) {
                parameters = incrementer.getNext(new JobParameters());
            }
        } else {
            List<JobExecution> previousExecutions = this.jobExplorer
                    .getJobExecutions(lastInstances.get(0));
            JobExecution previousExecution = CollectionUtils.isEmpty(previousExecutions) ? null : previousExecutions.get(0);
            if (previousExecution == null) {
                // Normally this will not happen - an instance exists with no executions
                if (incrementer != null) {
                    parameters = incrementer.getNext(new JobParameters());
                }
            } else if (isStoppedOrFailed(previousExecution) && job.isRestartable()) {
                // Retry a failed or stopped execution
                parameters = previousExecution.getJobParameters();
                // Non-identifying additional parameters can be removed to a retry
                removeNonIdentifying(additionals);
            } else if (incrementer != null) {
                // New instance so increment the parameters if we can
                parameters = incrementer.getNext(previousExecution.getJobParameters());
            }
        }
        return merge(parameters, additionals);
    }

    private boolean isStoppedOrFailed(JobExecution execution) {
        BatchStatus status = execution.getStatus();
        return (status == BatchStatus.STOPPED || status == BatchStatus.FAILED);
    }

    private void removeNonIdentifying(Map<String, JobParameter> parameters) {
        HashMap<String, JobParameter> copy = new HashMap<>(
                parameters);
        for (Map.Entry<String, JobParameter> parameter : copy.entrySet()) {
            if (!parameter.getValue().isIdentifying()) {
                parameters.remove(parameter.getKey());
            }
        }
    }

    private JobParameters merge(JobParameters parameters,
                                Map<String, JobParameter> additionals) {
        Map<String, JobParameter> merged = new HashMap<>();
        merged.putAll(parameters.getParameters());
        merged.putAll(additionals);
        parameters = new JobParameters(merged);
        return parameters;
    }

    private JobParameters getJobParameters(JobExecutionContext context) {
        final JobDataMap jobDataMap = context.getMergedJobDataMap();
        return converter.getJobParameters(jobDataMap);
    }

    protected org.springframework.batch.core.Job findJob(String jobName) throws NoSuchJobException {
        return this.jobRegistry.getJob(jobName);
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setJobRegistry(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobParametersConverter(JobParametersConverter converter) {
        this.converter = converter;
    }

}
