package com.nn.homework;


import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeworkApplication {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private ApplicationContext applicationContext;

  public static void main(String[] args) {
    SpringApplication.run(HomeworkApplication.class, args);
  }

/*  @Override
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
  }*/


}
