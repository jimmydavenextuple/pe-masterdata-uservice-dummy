package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.JobServiceException;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

  @Mock private JobsConsumerClient jobsConsumerClient;

  @InjectMocks private JobService jobService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void createJob() throws JobServiceException {
    when(jobsConsumerClient.createJob(any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.createJob(JobTypeEnum.UPLOAD_TRANSIT_TIMES, 9))
                .build());

    JobDto jobDto = jobService.createJob(TestUtil.ORG_ID, 9, JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    Assertions.assertNotNull(jobDto);
    Assertions.assertFalse(ObjectUtils.isEmpty(jobDto.getJobId()));
    verify(jobsConsumerClient, times(1)).createJob(any());
  }

  @Test
  void createJobFeignException() {
    when(jobsConsumerClient.createJob(any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.POST, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            JobServiceException.class,
            () -> jobService.createJob(TestUtil.ORG_ID, 9, JobTypeEnum.UPLOAD_TRANSIT_TIMES));
    Assertions.assertNotNull(exception);
    verify(jobsConsumerClient, times(1)).createJob(any());
  }

  @Test
  void createJobException() {
    when(jobsConsumerClient.createJob(any()))
        .thenThrow(new RuntimeException("Error while creating job"));

    Exception exception =
        Assertions.assertThrows(
            JobServiceException.class,
            () -> jobService.createJob(TestUtil.ORG_ID, 9, JobTypeEnum.UPLOAD_TRANSIT_TIMES));
    Assertions.assertNotNull(exception);
    verify(jobsConsumerClient, times(1)).createJob(any());
  }
}
