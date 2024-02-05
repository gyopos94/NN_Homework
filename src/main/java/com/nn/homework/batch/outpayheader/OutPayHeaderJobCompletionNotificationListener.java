package com.nn.homework.batch.outpayheader;


import com.nn.homework.domain.OutPayHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OutPayHeaderJobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(
      OutPayHeaderJobCompletionNotificationListener.class);

  private final JdbcTemplate jdbcTemplate;

  public OutPayHeaderJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!!OutPay_Header JOB FINISHED! Verify the results");

      jdbcTemplate
          .query("SELECT * FROM out_pay_header", new DataClassRowMapper<>(OutPayHeader.class))
          .forEach(opheader -> log.info("Found <{{}}> in the database.", opheader));
    }
  }
}