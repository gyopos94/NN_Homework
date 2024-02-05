package com.nn.homework.scheduler;

import com.nn.homework.service.JobLaunchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobsScheduler {

  private static final Logger log = LoggerFactory.getLogger(JobsScheduler.class);
  private static final String OUT_PAY_HEADER_JOB_NAME = "OutPayHeader";
  private static final String POLICY_JOB_NAME = "Policy";
  private static final String SUR_VALUE_JOB_NAME = "SurValue";

  private final JobLaunchingService jobLaunchingService;

  @Autowired
  public JobsScheduler(JobLaunchingService jobLaunchingService) {
    this.jobLaunchingService = jobLaunchingService;
  }

  @Scheduled(cron = "0 0/5 * * * ?") // 5 minutes
  public void runSurValueJob() {
    launchJob(SUR_VALUE_JOB_NAME);
  }

  @Scheduled(cron = "0 0/5 * * * ?") // 5 minutes
  public void runPolicyJob() {
    launchJob(POLICY_JOB_NAME);
  }

  @Scheduled(cron = "0 0/3 * * * ?") // 3 minutes
  public void runOutPayHeaderJob() {
    launchJob(OUT_PAY_HEADER_JOB_NAME);
  }

  private void launchJob(String jobName) {
    try {
      boolean isUnsuccessful = jobLaunchingService.launchJob(jobName);
      if (isUnsuccessful) {
        log.error("Job execution was unsuccessful: {}", jobName);
      } else {
        log.info("Successfully executed job: {}", jobName);
      }
    } catch (Exception e) {
      log.error("An error occurred while launching the job: {}", jobName, e);
    }
  }
}
