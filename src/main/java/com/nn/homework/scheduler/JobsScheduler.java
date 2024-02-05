package com.nn.homework.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobsScheduler {

  private static final Logger log = LoggerFactory.getLogger(JobsScheduler.class);
  private static final String TIME_PARAMETER_NAME = "time";
  private static final String JOB_EXECUTION_ALREADY_RUNNING_EXCEPTION_MSG = "JobExecutionAlreadyRunningException: The job is already running.";
  private static final String JOB_RESTART_EXCEPTION_MSG = "JobRestartException: A failure occurred in restarting the job.";
  private static final String JOB_INSTANCE_ALREADY_COMPLETE_EXCEPTION_MSG = "JobInstanceAlreadyCompleteException: The job has been already completed for the given parameters.";
  private static final String JOB_PARAMETERS_INVALID_EXCEPTION_MSG = "JobParametersInvalidException: The parameters provided for the job are invalid.";
  private static final String EXCEPTION_MSG = "Exception: An unexpected error occurred while running the job.";

  private final JobLauncher jobLauncher;
  private final Job importOutPayHeaderJob;
  private final Job importPolicyJob;
  private final Job importSurValueJob;
  private final SchedulingPropertiesConfiguration schedulingPropertiesConfiguration;

  public JobsScheduler(JobLauncher jobLauncher,
      Job importOutPayHeaderJob,
      Job importPolicyJob,
      Job importSurValueJob,
      SchedulingPropertiesConfiguration schedulingPropertiesConfiguration) {
    this.jobLauncher = jobLauncher;
    this.importOutPayHeaderJob = importOutPayHeaderJob;
    this.importPolicyJob = importPolicyJob;
    this.importSurValueJob = importSurValueJob;
    this.schedulingPropertiesConfiguration = schedulingPropertiesConfiguration;
  }

  @Scheduled(cron = "#{schedulingPropertiesConfiguration.surValue.cron}")
  public void runSurValueJob() {
    runJob(importSurValueJob,
        new JobParametersBuilder().addLong(TIME_PARAMETER_NAME, System.currentTimeMillis())
            .toJobParameters());
  }

  @Scheduled(cron = "#{schedulingPropertiesConfiguration.policy.cron}")
  public void runPolicyJob() {
    runJob(importPolicyJob,
        new JobParametersBuilder().addLong(TIME_PARAMETER_NAME, System.currentTimeMillis())
            .toJobParameters());
  }

  @Scheduled(cron = "#{schedulingPropertiesConfiguration.outPayHeader.cron}")
  public void runOutPayHeaderJob() {
    runJob(importOutPayHeaderJob,
        new JobParametersBuilder().addLong(TIME_PARAMETER_NAME, System.currentTimeMillis())
            .toJobParameters());
  }

  private void runJob(Job job, JobParameters parameters) {
    try {
      jobLauncher.run(job, parameters);
    } catch (JobExecutionAlreadyRunningException e) {
      log.error(JOB_EXECUTION_ALREADY_RUNNING_EXCEPTION_MSG, e);
    } catch (JobRestartException e) {
      log.error(JOB_RESTART_EXCEPTION_MSG, e);
    } catch (JobInstanceAlreadyCompleteException e) {
      log.error(JOB_INSTANCE_ALREADY_COMPLETE_EXCEPTION_MSG, e);
    } catch (JobParametersInvalidException e) {
      log.error(JOB_PARAMETERS_INVALID_EXCEPTION_MSG, e);
    } catch (Exception e) {
      log.error(EXCEPTION_MSG, e);
    }
  }
}
