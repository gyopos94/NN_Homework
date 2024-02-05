package com.nn.homework.scheduler;

import com.nn.homework.service.JobLaunchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task executor for batch jobs.
 *
 * <p>This component is responsible for the scheduled execution of batch jobs
 * within the application. It utilizes the {@link JobLaunchingService} to launch
 * predefined jobs at scheduled intervals. The scheduling for each job is defined
 * by cron expressions, allowing for precise control over job execution timing.</p>
 *
 * <p>Jobs include processing of various data such as 'OutPayHeader', 'Policy', and
 * 'SurValue'. Each job has a specific schedule defined by a cron expression, ensuring
 * that the jobs are executed at the appropriate times without manual intervention.</p>
 *
 * <p>The class handles the initiation of job launches and logs the outcome of each
 * attempt, providing visibility into the job execution process and facilitating
 * troubleshooting of any issues that may arise during job execution.</p>
 */
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

  @Scheduled(cron = "${scheduler.cron.survalue}")
  public void runSurValueJob() {
    launchJob(SUR_VALUE_JOB_NAME);
  }

  @Scheduled(cron = "${scheduler.cron.policy}")
  public void runPolicyJob() {
    launchJob(POLICY_JOB_NAME);
  }

  @Scheduled(cron = "${scheduler.cron.outpayheader}")
  public void runOutPayHeaderJob() {
    launchJob(OUT_PAY_HEADER_JOB_NAME);
  }

  /**
   * Helper method to launch a job by name.
   *
   * <p>This method attempts to launch a job specified by its name using the
   * {@link JobLaunchingService}. It logs the outcome of the launch attempt,
   * providing information on successful execution or any errors that occurred.</p>
   *
   * @param jobName The name of the job to launch.
   */
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
