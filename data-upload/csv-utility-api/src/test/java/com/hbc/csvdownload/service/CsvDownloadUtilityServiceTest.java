package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityServiceTest {

  @Mock private JobsConsumerService jobsConsumerService;
  @Mock private JobsDashboardService jobsDashboardService;
  @Mock private PostalCodeTimeZoneService postalCodeTimeZoneService;
  @Mock private TransitService transitService;
  @Mock private ProcessingTimeBuffersService processingTimeBuffersService;
  @InjectMocks private CsvDownloadUtilityService csvDownloadUtilityService;
  @InjectMocks private TestUtil testUtil;

  @Test
  void downloadLogsAsCsvForProcessingLeadTimes() throws CommonServiceException {
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenReturn(testUtil.getJobDto());
    when(jobsDashboardService.getJobRecords(TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS))
        .thenReturn(testUtil.getJobRecordsForProcessingLeadTimes());

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS);
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTime() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getJobRecordsForTransitTimes()));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTimeEmptyErrorMessage() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto.setErrorMessage("");
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(recordStatusDto));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForTransitTimeFeignRetryableException() throws CommonServiceException {
    JobDto jobDto = testUtil.getJobDto();
    RecordStatusDto recordStatusDto = testUtil.getJobRecordsForTransitTimes();
    recordStatusDto.setException("feign.RetryableException");
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(jobsDashboardService.getJobRecords(anyString(), anyString(), any()))
        .thenReturn(List.of(recordStatusDto));

    String csvContent =
        csvDownloadUtilityService.downloadLogsAsCsv(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadTransitTimeAndProcessingLeadTimeCsvExceptionTest() throws CommonServiceException {
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenReturn(testUtil.getJobDto2());

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsv(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, TestUtil.STATUS));
    Assertions.assertNotNull(exception);
  }

  @Test
  void downloadTransitTimesForSourceAndDestinationRegion()
      throws PostalCodeTimezoneServiceException, TransitServiceException,
          CsvDownloadUtilityServiceException {
    when(postalCodeTimeZoneService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneService.getFSAsByOrgIdAndState(TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA));

    when(transitService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    String csvContents =
        csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
            TestUtil.ORG_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.SOURCE_REGION,
            TestUtil.DESTINATION_REGION);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneService, times(2)).getFSAsByOrgIdAndState(anyString(), anyString());
    verify(transitService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }

  @Test
  void downloadTransitTimesForSourceAndDestinationRegionException()
      throws PostalCodeTimezoneServiceException, TransitServiceException {
    when(postalCodeTimeZoneService.getFSAsByOrgIdAndState(
            TestUtil.ORG_ID, TestUtil.DESTINATION_REGION))
        .thenReturn(List.of(TestUtil.DESTINATION_FSA));

    when(postalCodeTimeZoneService.getFSAsByOrgIdAndState(TestUtil.ORG_ID, TestUtil.SOURCE_REGION))
        .thenReturn(List.of(TestUtil.SOURCE_FSA + "1"));

    when(transitService.getTransitDetails(anyString(), anyString(), any()))
        .thenReturn(List.of(testUtil.getTransitResponse(1.5F)));

    Exception exception =
        Assertions.assertThrows(
            CsvDownloadUtilityServiceException.class,
            () ->
                csvDownloadUtilityService.downloadTransitTimesForSourceAndDestinationRegion(
                    TestUtil.ORG_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.SOURCE_REGION,
                    TestUtil.DESTINATION_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimeZoneService, times(2)).getFSAsByOrgIdAndState(anyString(), anyString());
    verify(transitService, times(1)).getTransitDetails(anyString(), anyString(), any());
  }

  @Test
  void downloadMarketRegionForOrgIdAndCountry_Test()
      throws PostalCodeTimezoneServiceException, CsvDownloadUtilityServiceException {
    when(postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeTimezoneDto()));

    String csvContents =
        csvDownloadUtilityService.downloadMarketRegionForOrgIdAndCountry(
            TestUtil.ORG_ID, TestUtil.COUNTRY);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(postalCodeTimeZoneService, times(1))
        .getPostalCodeTimeZoneByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void downloadProcessingTimeBuffersByOrgIdTest() throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponse(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any())).thenReturn(responseList);

    File file = csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(TestUtil.ORG_ID);

    Assertions.assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any());
  }

  @Test
  void downloadProcessingTimeBuffersByOrgIdWithEmptyValuesTest() throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponseEmptyValues(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any())).thenReturn(responseList);

    File file = csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(TestUtil.ORG_ID);

    Assertions.assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any());
  }
}
