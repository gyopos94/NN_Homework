package com.nn.homework.controller;

import com.nn.homework.exception.JobLaunchException;
import com.nn.homework.service.JobLaunchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for managing job launches.
 *
 * <p>This controller provides an endpoint for launching batch jobs by name.
 * It uses the {@link JobLaunchingService} to initiate the execution of specified jobs.
 * The controller handles job launch requests, processes them using the service layer,
 * and returns appropriate responses based on the outcome of the job launch attempt.</p>
 *
 * <p>Endpoints provided by this controller allow for flexible job management through
 * web requests, enabling clients to trigger batch processing jobs as needed.</p>
 */
@RestController
public class JobController {

  private static final Logger log = LoggerFactory.getLogger(JobController.class);

  private static final String JOB_FAILED_RESPONSE = "Job failed to complete successfully.";
  private static final String JOB_SUCCESS_RESPONSE = "Job launched successfully.";
  private static final String ERROR_LAUNCHING_JOB_RESPONSE = "Error launching job: ";

  private final JobLaunchingService jobLaunchingService;

  public JobController(JobLaunchingService jobLaunchingService) {
    this.jobLaunchingService = jobLaunchingService;
  }

  /**
   * Launches a batch job based on the provided job name.
   *
   * <p>This endpoint receives a request to launch a job, identified by its name.
   * It attempts to launch the specified job and returns a response indicating the
   * outcome. If the job fails to launch or completes unsuccessfully, an appropriate
   * error response is returned. In case of successful launch and completion, a success
   * response is provided.</p>
   *
   * @param jobName The name of the job to launch.
   * @return A {@link ResponseEntity} with a message indicating the outcome of the launch attempt.
   */
  @GetMapping("/launch-job")
  public ResponseEntity<String> launchJob(@RequestParam String jobName) {
    log.debug("JobController.launchJob called with jobName={}", jobName);
    try {
      boolean jobFailed = jobLaunchingService.launchJob(jobName);
      if (jobFailed) {
        return ResponseEntity.internalServerError().body(JOB_FAILED_RESPONSE);
      } else {
        return ResponseEntity.ok(JOB_SUCCESS_RESPONSE);
      }
    } catch (JobLaunchException e) {
      return ResponseEntity.internalServerError()
          .body(ERROR_LAUNCHING_JOB_RESPONSE + e.getMessage());
    }
  }
}
