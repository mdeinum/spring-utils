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

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author marten
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/simple-batch-quartz-config.xml"})
public class SimpleJobLauncherJobTest {

    @Autowired
    private JobExplorer jobRepository;

    @Test
    public void shouldLaunchJob() throws Exception {
        Thread.sleep(1500);
        assertThat(jobRepository.getJobInstanceCount("helloWorld")).isEqualTo(1);
        List<JobInstance> jobInstances = jobRepository.getJobInstances("helloWorld", 0, 1);
        for (JobInstance instance : jobInstances) {
            System.out.println(instance);
        }
    }


    public static class SystemOutTasklet implements Tasklet {

        private String message;

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            System.out.println(message);
            return RepeatStatus.FINISHED;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}