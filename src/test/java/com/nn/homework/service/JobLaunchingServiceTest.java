package com.nn.homework.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.nn.homework.exception.InvalidJobParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

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
  void throwInvalidJobParameterExceptionForUnknownJob() {
    // Arrange & Act
    Exception exception = assertThrows(InvalidJobParameterException.class,
        () -> jobLaunchingService.launchJob("UnknownJob"));

    // Verify
    assertTrue(exception.getMessage().contains("Invalid job parameter"));
    verifyNoInteractions(jobLauncher);
  }
}
