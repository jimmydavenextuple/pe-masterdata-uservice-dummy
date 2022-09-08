package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.jobs.framework.common.clients.JobsConsumerClient;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobsConsumerServiceTest {
  @Mock private JobsConsumerClient jobsConsumerClient;
  @InjectMocks private JobsConsumerService jobsConsumerService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getJobTest() throws CommonServiceException {
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobDto()).build());

    JobDto jobDto = jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID);
    Assertions.assertFalse(jobDto.getJobId().isEmpty());
  }

  @Test
  void getJobEmptyTest() {
    when(jobsConsumerClient.getJob(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(null).build());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID));
    Assertions.assertNotNull(exception);
  }

  @Test
  void getJobDtoEmptyTest() {
    when(jobsConsumerClient.getJob(anyString(), anyString())).thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID));
    Assertions.assertNotNull(exception);
  }
}
