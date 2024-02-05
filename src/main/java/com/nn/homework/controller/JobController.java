package com.nn.homework.controller;

import com.nn.homework.exception.InvalidJobParameterException;
import com.nn.homework.scheduler.JobsScheduler;
import com.nn.homework.service.JobLaunchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

  private static final Logger log = LoggerFactory.getLogger(JobController.class);

  private final JobLaunchingService jobLaunchingService;

  public JobController(JobLaunchingService jobLaunchingService) {
    this.jobLaunchingService = jobLaunchingService;
  }

  @GetMapping("/launch-job/{jobName}")
  public ResponseEntity<String> launchJob(@PathVariable String jobName) {
    log.debug("JobController.launchJob called");
    try {
      jobLaunchingService.launchJob(jobName);
      return ResponseEntity.ok("Job launched successfully.");
    } catch (InvalidJobParameterException e) {
      return ResponseEntity.badRequest().body("Invalid job parameter: " + jobName);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error launching job: " + e.getMessage());
    }
  }
}
