package com.nn.homework.batch.policy;


import com.nn.homework.domain.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PolicyJobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(
      PolicyJobCompletionNotificationListener.class);

  private final JdbcTemplate jdbcTemplate;

  public PolicyJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("policy JOB FINISHED! Verify the results");

      jdbcTemplate
          .query("SELECT * FROM policy", new DataClassRowMapper<>(Policy.class))
          .forEach(policy -> log.info("Found <{{}}> in the database.", policy));
    }
  }
}