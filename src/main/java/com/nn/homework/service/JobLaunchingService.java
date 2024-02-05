package com.nn.homework.service;

import com.nn.homework.exception.InvalidJobParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class JobLaunchingService {

  private static final Logger log = LoggerFactory.getLogger(JobLaunchingService.class);

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


  public void launchJob(String jobName) {
    Job jobToLaunch;
    switch (jobName) {
      case "OutPayHeader":
        jobToLaunch = outPayHeaderJob;
        break;
      case "Policy":
        jobToLaunch = policyJob;
        break;
      case "SurValue":
        jobToLaunch = surValueJob;
        break;
      default:
        throw new InvalidJobParameterException("Invalid job parameter: " + jobName);
    }
    try {
      jobLauncher.run(jobToLaunch,
          new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters());
    } catch (JobExecutionAlreadyRunningException e) {
      log.error("JobExecutionAlreadyRunningException: The job is already running.", e);
    } catch (JobRestartException e) {
      log.error("JobRestartException: A failure occurred in restarting the job.", e);
    } catch (JobInstanceAlreadyCompleteException e) {
      log.error(
          "JobInstanceAlreadyCompleteException: The job has been already completed for the given parameters.",
          e);
    } catch (JobParametersInvalidException e) {
      log.error("JobParametersInvalidException: The parameters provided for the job are invalid.",
          e);
    } catch (Exception e) {
      log.error("Exception: An unexpected error occurred while running the job.", e);
    }
  }
}