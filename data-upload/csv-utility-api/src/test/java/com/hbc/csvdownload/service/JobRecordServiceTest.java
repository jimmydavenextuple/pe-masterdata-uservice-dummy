package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
 class JobRecordServiceTest {
  @Mock private JobsDashboardClient jobsDashboardClient;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private JobRecordsService jobRecordsService;

  @Test
  void getJobRecordsTest() throws CommonServiceException {
    when(jobsDashboardClient.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getJobRecords()).build());
    List<RecordStatusDto> recordStatusDtoList =
        jobRecordsService.getJobRecords(TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS);
    Assertions.assertFalse(CollectionUtils.isEmpty(recordStatusDtoList));
  }

  @Test
  void getJobRecordEmptyListTest(){
      when(jobsDashboardClient.getJobRecords(anyString(),anyString(),any()))
              .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());
      Exception exception =
              Assertions.assertThrows(
                      CommonServiceException.class,
                      () ->
                              jobRecordsService.getJobRecords(
                                      TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
      Assertions.assertNotNull(exception);
  }
  @Test
  void getJobRecordsNullResponseTest() {
    when(jobsDashboardClient.getJobRecords(anyString(), anyString(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                jobRecordsService.getJobRecords(TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
    Assertions.assertNotNull(exception);
  }
}
