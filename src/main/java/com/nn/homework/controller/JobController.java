package com.nn.homework.controller;

import com.nn.homework.exception.JobLaunchException;
import com.nn.homework.service.JobLaunchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
