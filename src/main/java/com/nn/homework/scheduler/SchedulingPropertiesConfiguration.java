package com.nn.homework.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "scheduling")
@Validated
@Data
public class SchedulingPropertiesConfiguration {

  private JobProperties surValue;
  private JobProperties policy;
  private JobProperties outPayHeader;

  @Data
  public static class JobProperties {
    private String cron;
  }
}