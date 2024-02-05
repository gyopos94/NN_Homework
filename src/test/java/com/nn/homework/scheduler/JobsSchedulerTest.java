package com.nn.homework.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobsSchedulerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job importOutPayHeaderJob;

  @Mock
  private Job importPolicyJob;

  @Mock
  private Job importSurValueJob;

  @InjectMocks
  private JobsScheduler jobsScheduler;

  @Captor
  private ArgumentCaptor<JobParameters> jobParametersCaptor;

  @BeforeEach
  void setUp() throws Exception {
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(new JobExecution(1L));
  }

  @Test
  void runSurValueJobTest() throws Exception {
    jobsScheduler.runSurValueJob();
    verify(jobLauncher).run(eq(importSurValueJob), jobParametersCaptor.capture());
  }

  @Test
  void runPolicyJobTest() throws Exception {
    jobsScheduler.runPolicyJob();
    verify(jobLauncher).run(eq(importPolicyJob), jobParametersCaptor.capture());
  }

  @Test
  void runOutPayHeaderJobTest() throws Exception {
    jobsScheduler.runOutPayHeaderJob();
    verify(jobLauncher).run(eq(importOutPayHeaderJob), jobParametersCaptor.capture());
  }
}
