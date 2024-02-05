package com.nn.homework;


import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class HomeworkApplication implements CommandLineRunner {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private ApplicationContext applicationContext;

  public static void main(String[] args) {
    SpringApplication.run(HomeworkApplication.class, args);}

  @Override
  public void run(String... args) throws Exception {
    Map<String, Job> jobs = applicationContext.getBeansOfType(Job.class);

    jobs.forEach((beanName, job) -> {
      try {
        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("time", System.currentTimeMillis())
            .toJobParameters();
        jobLauncher.run(job, jobParameters);
      } catch (JobExecutionException e) {
        System.out.println("Failed to execute job " + beanName);
        e.printStackTrace();
      }
    });
  }


}
