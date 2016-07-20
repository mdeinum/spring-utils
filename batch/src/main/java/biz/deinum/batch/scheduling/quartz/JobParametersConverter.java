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

import org.quartz.JobDataMap;
import org.springframework.batch.core.JobParameters;

/**
 * A factory for {@link JobParameters} instances. A job can be executed with
 * many possible runtime parameters, which identify the instance of the job.
 * This converter allows job parameters to be converted to and from a JobDataMap.
 *
 * @author Marten Deinum
 *
 * @see org.springframework.batch.core.JobParametersBuilder
 * @see org.springframework.batch.core.converter.JobParametersConverter
 */
public interface JobParametersConverter {

    /**
     * Get a new {@link JobParameters} instance. If given null, or an empty
     * properties, an empty JobParameters will be returned.
     *
     * @param jobDataMap the merged {@code JobDataMap} from the quarts job.
     * @return a {@link JobParameters} properties converted to the correct
     * types.
     */
    JobParameters getJobParameters(JobDataMap jobDataMap);

    /**
     * The inverse operation: get a {@link JobDataMap} instance. If given null
     * or empty JobParameters, an empty JobDataMap should be returned.
     *
     * @param params
     * @return a representation of the parameters as JobDataMap
     */

    JobDataMap getJobDataMap(JobParameters params);
}
