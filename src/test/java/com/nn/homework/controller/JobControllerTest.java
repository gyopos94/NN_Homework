package com.nn.homework.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nn.homework.exception.JobLaunchException;
import com.nn.homework.service.JobLaunchingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
    doNothing().when(jobLaunchingService).launchJob(anyString());
    mockMvc.perform(get("/launch-job/{jobName}", "SurValue")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Job launched successfully."));
  }

  @Test
  void whenLaunchJobFails_thenReturnsErrorMessage() throws Exception {
    doThrow(new JobLaunchException("Error launching job", null)).when(jobLaunchingService)
        .launchJob(anyString());
    mockMvc.perform(get("/launch-job/{jobName}", "invalidJobName")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error launching job: Error launching job"));
  }
}
