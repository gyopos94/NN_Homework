package com.nn.homework.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nn.homework.exception.JobLaunchException;
import com.nn.homework.service.JobLaunchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JobsSchedulerTest {

  @Mock
  private JobLaunchingService jobLaunchingService;

  @InjectMocks
  private JobsScheduler jobsScheduler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRunSurValueJob() throws JobLaunchException {
    jobsScheduler.runSurValueJob();
    verify(jobLaunchingService, times(1)).launchJob("SurValue");
  }

  @Test
  void testRunPolicyJob() throws JobLaunchException {
    jobsScheduler.runPolicyJob();
    verify(jobLaunchingService, times(1)).launchJob("Policy");
  }

  @Test
  void testRunOutPayHeaderJob() throws JobLaunchException {
    jobsScheduler.runOutPayHeaderJob();
    verify(jobLaunchingService, times(1)).launchJob("OutPayHeader");
  }
}
