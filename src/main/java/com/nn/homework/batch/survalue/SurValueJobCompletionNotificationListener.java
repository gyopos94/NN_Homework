package com.nn.homework.batch.survalue;


import com.nn.homework.domain.SurValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SurValueJobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(
      SurValueJobCompletionNotificationListener.class);

  private final JdbcTemplate jdbcTemplate;

  public SurValueJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("policy JOB FINISHED! Verify the results");

      jdbcTemplate
          .query("SELECT * FROM sur_value", new DataClassRowMapper<>(SurValue.class))
          .forEach(surValue -> log.info("Found <{{}}> in the database.", surValue));
    }
  }
}