package com.nn.homework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nn.homework.exception.JobLaunchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

@ExtendWith(MockitoExtension.class)
class JobLaunchingServiceTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job outPayHeaderJob;

  @Mock
  private Job policyJob;

  @Mock
  private Job surValueJob;

  @InjectMocks
  private JobLaunchingService jobLaunchingService;

  @BeforeEach
  void setUp() {
  }

  @Test
  void launchOutPayHeaderJobSuccessfully() throws Exception {
    // Act
    jobLaunchingService.launchJob("OutPayHeader");

    // Verify
    verify(jobLauncher).run(eq(outPayHeaderJob), any(JobParameters.class));
  }

  @Test
  void launchPolicyJobSuccessfully() throws Exception {
    // Act
    jobLaunchingService.launchJob("Policy");

    // Verify
    verify(jobLauncher).run(eq(policyJob), any(JobParameters.class));
  }

  @Test
  void launchSurValueJobSuccessfully() throws Exception {
    // Act
    jobLaunchingService.launchJob("SurValue");

    // Verify
    verify(jobLauncher).run(eq(surValueJob), any(JobParameters.class));
  }

  @Test
  void testLaunchJobWithJobExecutionAlreadyRunningException()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    doThrow(new JobExecutionAlreadyRunningException("The job is already running.")).when(jobLauncher).run(any(Job.class), any());

    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("OutPayHeader"));
    assertEquals("JobExecutionAlreadyRunningException: The job is already running.", exception.getMessage());
  }

  @Test
  void testLaunchJobWithJobRestartException()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    doThrow(new JobRestartException("A failure occurred in restarting the job.")).when(jobLauncher).run(any(Job.class), any());

    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("Policy"));
    assertEquals("JobRestartException: A failure occurred in restarting the job.", exception.getMessage());
  }

  @Test
  void testLaunchJobWithJobInstanceAlreadyCompleteException()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    doThrow(new JobInstanceAlreadyCompleteException("The job has been already completed for the given parameters.")).when(jobLauncher).run(any(Job.class), any());

    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("SurValue"));
    assertEquals("JobInstanceAlreadyCompleteException: The job has been already completed for the given parameters.", exception.getMessage());
  }

  @Test
  void testLaunchJobWithJobParametersInvalidException()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    doThrow(new JobParametersInvalidException("The parameters provided for the job are invalid.")).when(jobLauncher).run(any(Job.class), any());

    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("OutPayHeader"));
    assertEquals("JobParametersInvalidException: The parameters provided for the job are invalid.", exception.getMessage());
  }

  @Test
  void testLaunchJobWithGenericException()
      throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    doThrow(new RuntimeException("An unexpected error occurred while running the job.")).when(jobLauncher).run(any(Job.class), any());

    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("Policy"));
    assertEquals("Exception: An unexpected error occurred while running the job.", exception.getMessage());
  }

  @Test
  void testLaunchJobWithInvalidJobParameter() {
    JobLaunchException exception = assertThrows(JobLaunchException.class, () -> jobLaunchingService.launchJob("InvalidJobName"));
    assertEquals("Invalid job parameter: InvalidJobName", exception.getMessage());
  }
}
