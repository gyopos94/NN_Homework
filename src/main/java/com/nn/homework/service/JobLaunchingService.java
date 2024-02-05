package com.nn.homework.service;

import com.nn.homework.exception.JobLaunchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service for launching batch jobs within the application context.
 *
 * <p>This service provides functionality to launch batch jobs by name, utilizing
 * Spring Batch's {@link JobLauncher} to initiate job executions with the current system time as a
 * parameter. It supports launching specific predefined jobs, namely 'OutPayHeader', 'Policy', and
 * 'SurValue', identified by their respective job names.</p>
 *
 * <p>The service encapsulates error handling for common job execution issues, such as
 * jobs already running, jobs being complete, and invalid job parameters. It translates these
 * scenarios into a {@link JobLaunchException}, providing a consistent error handling mechanism for
 * clients of this service.</p>
 */
@Service
public class JobLaunchingService {

  private static final Logger log = LoggerFactory.getLogger(JobLaunchingService.class);

  private static final String JOB_PARAM_TIME = "time";
  private static final String OUT_PAY_HEADER_JOB_NAME = "OutPayHeader";
  private static final String POLICY_JOB_NAME = "Policy";
  private static final String SUR_VALUE_JOB_NAME = "SurValue";

  private static final String JOB_EXECUTION_ALREADY_RUNNING_EXCEPTION_MSG = "JobExecutionAlreadyRunningException: The job is already running.";
  private static final String JOB_RESTART_EXCEPTION_MSG = "JobRestartException: A failure occurred in restarting the job.";
  private static final String JOB_INSTANCE_ALREADY_COMPLETE_EXCEPTION_MSG = "JobInstanceAlreadyCompleteException: The job has been already completed for the given parameters.";
  private static final String JOB_PARAMETERS_INVALID_EXCEPTION_MSG = "JobParametersInvalidException: The parameters provided for the job are invalid.";
  private static final String UNEXPECTED_ERROR_MSG = "Exception: An unexpected error occurred while running the job.";

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier("importOutPayHeaderJob")
  private Job outPayHeaderJob;

  @Autowired
  @Qualifier("importPolicyJob")
  private Job policyJob;

  @Autowired
  @Qualifier("importSurValueJob")
  private Job surValueJob;

  /**
   * Launches a batch job based on the given job name.
   *
   * <p>This method selects the appropriate job to launch based on the provided
   * job name and initiates its execution with a unique parameter ("time") to ensure the job
   * instance is unique. It handles specific job execution exceptions by logging the error and
   * rethrowing a {@link JobLaunchException} with a message appropriate to the caught
   * exception.</p>
   *
   * @param jobName The name of the job to be launched.
   * @return {@code true} if the job execution was unsuccessful, otherwise {@code false}.
   * @throws JobLaunchException If an error occurs during job launching or if an unexpected
   *                            exception is caught.
   */
  public boolean launchJob(String jobName) throws JobLaunchException {
    Job jobToLaunch;
    switch (jobName) {
      case OUT_PAY_HEADER_JOB_NAME:
        jobToLaunch = outPayHeaderJob;
        break;
      case POLICY_JOB_NAME:
        jobToLaunch = policyJob;
        break;
      case SUR_VALUE_JOB_NAME:
        jobToLaunch = surValueJob;
        break;
      default:
        throw new JobLaunchException("Invalid job parameter: " + jobName, null);
    }

    try {
      JobExecution jobExecution = jobLauncher.run(jobToLaunch,
          new JobParametersBuilder().addLong(JOB_PARAM_TIME, System.currentTimeMillis())
              .toJobParameters());

      return jobExecution.getStatus().isUnsuccessful();
    } catch (JobExecutionAlreadyRunningException e) {
      log.error(JOB_EXECUTION_ALREADY_RUNNING_EXCEPTION_MSG, e);
      throw new JobLaunchException(JOB_EXECUTION_ALREADY_RUNNING_EXCEPTION_MSG, e);
    } catch (JobRestartException e) {
      log.error(JOB_RESTART_EXCEPTION_MSG, e);
      throw new JobLaunchException(JOB_RESTART_EXCEPTION_MSG, e);
    } catch (JobInstanceAlreadyCompleteException e) {
      log.error(JOB_INSTANCE_ALREADY_COMPLETE_EXCEPTION_MSG, e);
      throw new JobLaunchException(JOB_INSTANCE_ALREADY_COMPLETE_EXCEPTION_MSG, e);
    } catch (JobParametersInvalidException e) {
      log.error(JOB_PARAMETERS_INVALID_EXCEPTION_MSG, e);
      throw new JobLaunchException(JOB_PARAMETERS_INVALID_EXCEPTION_MSG, e);
    } catch (Exception e) {
      throw new JobLaunchException(UNEXPECTED_ERROR_MSG, e);
    }
  }
}