package com.nn.homework.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {SchedulingPropertiesConfigurationTest.TestConfig.class})
class SchedulingPropertiesConfigurationTest {

  @Autowired
  private SchedulingPropertiesConfiguration schedulingProperties;

  @Test
  void testCronExpressionsLoadedCorrectly() {
    assertEquals("0 0 1 * * SUN", schedulingProperties.getSurValue().getCron());
    assertEquals("0 0 1 * * ?", schedulingProperties.getPolicy().getCron());
    assertEquals("0 0 1 * * ?", schedulingProperties.getOutPayHeader().getCron());
  }

  @Configuration
  @EnableConfigurationProperties(SchedulingPropertiesConfiguration.class)
  static class TestConfig {
  }
}
