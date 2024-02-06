package com.nn.homework.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nn.homework.exception.JobLaunchException;
import com.nn.homework.service.JobLaunchingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(SpringExtension.class)
@WebMvcTest(JobController.class)
class JobControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JobLaunchingService jobLaunchingService;

  @MockBean
  private JobLauncher jobLauncher;

  @Test
  void whenLaunchJobSuccessfully_thenReturnsSuccessMessage() throws Exception {
    when(jobLaunchingService.launchJob(anyString())).thenReturn(false); // Simulate successful job launch
    mockMvc.perform(get("/api/v1/launch-job").param("jobName", "SurValue")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Job launched successfully."));
  }

  @Test
  void whenLaunchJobFails_thenReturnsFailureMessage() throws Exception {
    when(jobLaunchingService.launchJob(anyString())).thenReturn(true); // Simulate failed job launch
    mockMvc.perform(get("/api/v1/launch-job").param("jobName", "invalidJobName")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Job failed to complete successfully."));
  }

  @Test
  void whenLaunchJobThrowsException_thenReturnsErrorMessage() throws Exception {
    doThrow(new JobLaunchException("Error launching job", null)).when(jobLaunchingService).launchJob(anyString());
    mockMvc.perform(get("/api/v1/launch-job").param("jobName", "exceptionJobName")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error launching job: Error launching job"));
  }
}
