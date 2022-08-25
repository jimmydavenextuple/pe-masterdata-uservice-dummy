package com.hbc.jobs.consumers.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hbc.jobs.consumers.common.TestUtil;
import com.hbc.jobs.consumers.exception.JobDashboardException;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class JobDashboardServiceTest {

  @InjectMocks private JobDashboardService jobDashboardService;

  @InjectMocks private TestUtil testUtil;

  @Mock private KafkaTemplate<String, RecordStatusDto> kafkaTemplate;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    MockMvcBuilders.standaloneSetup(jobDashboardService).build();
    ReflectionTestUtils.setField(jobDashboardService, "resultPublishTopicName", "topicName");
  }

  @Test
  void publishJobRecordException() {
    RecordStatusDto recordStatusDto =
        testUtil.createRecordStatus(
            TestUtil.JOB_ID,
            TestUtil.ORG_ID,
            ApiStatusEnum.SUCCESS,
            HttpStatus.OK,
            "user123",
            JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES,
            3);

    when(kafkaTemplate.send("topicName", recordStatusDto)).thenThrow(new RuntimeException());

    JobDashboardException exception =
        assertThrows(
            JobDashboardException.class,
            () -> jobDashboardService.publishJobRecord(recordStatusDto));

    Assertions.assertEquals(
        "Exception while publishing the job record", exception.getMessage(), "Expected Error");

    Assertions.assertEquals(recordStatusDto.getJobId(), exception.getJobId(), "Expected Error");
  }
}
