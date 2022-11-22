package com.hbc.csvdownload.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.base.PagePayload.Pagination;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.common.pojo.DownloadNodeCarrierServiceAndServiceOptionPojo;
import com.hbc.csvdownload.exception.CarrierServiceException;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.csvdownload.service.v1.ProcessingRequestFactory;
import com.hbc.csvdownload.service.v1.impl.CalendarProcessingRequestImpl;
import com.hbc.csvdownload.service.v1.impl.TransitTimeProcessingRequestImpl;
import com.hbc.dataupload.common.feign.DataUploadFeign;
import com.hbc.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.hbc.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.hbc.jobs.framework.common.domain.enums.ApiStatusEnum;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.node.domain.dto.NodeDto;
import com.hbc.transit.domain.feign.TransitBufferFeign;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
class CsvDownloadUtilityServiceTest {

  @Mock private JobsConsumerService jobsConsumerService;
  @Mock private JobsDashboardService jobsDashboardService;
  @Mock private PostalCodeTimeZoneService postalCodeTimeZoneService;
  @Mock private TransitService transitService;
  @Mock private DataUploadFeign dataUploadFeign;

  @Mock private CarrierService carrierService;
  @Mock private CalenderService calenderService;
  @Mock private ProcessingTimeBuffersService processingTimeBuffersService;
  @Mock private NodeService nodeService;
  @Mock private NodeCarrierService nodeCarrierService;
  @Mock private ProcessingRequestFactory processingRequestFactory;
  @Mock private CalendarProcessingRequestImpl calendarProcessingRequest;
  @Mock private TransitTimeProcessingRequestImpl transitTimeProcessingRequest;
  @Mock private TransitBufferFeign transitBufferFeign;
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
  void downloadLogsAsCsvForCalendarV1()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_CALENDER))
        .thenReturn(calendarProcessingRequest);
    when(calendarProcessingRequest.downloadErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV1(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForCalendarV1UploadTransitTimes()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID)).thenReturn(jobDto);
    when(processingRequestFactory.getModuleByJobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES))
        .thenReturn(transitTimeProcessingRequest);
    when(transitTimeProcessingRequest.downloadTransitTimeErrorLogs(any(), any()))
        .thenReturn(testUtil.getPreSignedUrlResponse());

    var csvContent =
        csvDownloadUtilityService.downloadLogsAsCsvV1(
            TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name()));
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContent));
  }

  @Test
  void downloadLogsAsCsvForCalendarV1Exception()
      throws CommonServiceException, JobSubmissionException, IOException {
    JobDto jobDto = testUtil.getJobDto();
    jobDto.setJobType(JobTypeEnum.UPLOAD_CALENDER);
    when(jobsConsumerService.getJob(TestUtil.JOB_ID, TestUtil.ORG_ID))
        .thenThrow(new RuntimeException("Error while fetching job"));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                csvDownloadUtilityService.downloadLogsAsCsvV1(
                    TestUtil.JOB_ID, TestUtil.ORG_ID, Optional.of(ApiStatusEnum.FAILURE.name())));
    Assertions.assertNotNull(exception);
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
  void downloadMarketRegionForOrgIdAndCountry_Test() throws PostalCodeTimezoneServiceException {
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
  void downloadCarrierServiceData_Test()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderService.getCarrierServiceCalender(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse()));

    when(transitService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceData_TestNoCalenderExist()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderService.getCarrierServiceCalender(anyString(), anyString()))
        .thenThrow(CarrierServiceException.class);

    when(transitService.getTransitTimeEntries(anyString(), anyString()))
        .thenReturn(testUtil.getTransitTimeEntriesDto());
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);
    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadCarrierServiceData_TestErrorTransitEntries()
      throws CarrierServiceException, TransitServiceException, IOException {
    when(carrierService.getCarrierService(anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceResponse()));
    when(calenderService.getCarrierServiceCalender(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCarrierServiceCalendarResponse()));

    when(transitService.getTransitTimeEntries(anyString(), anyString()))
        .thenThrow(TransitServiceException.class);
    File csvContents = csvDownloadUtilityService.downloadCarrierServiceDataCSV(TestUtil.ORG_ID);

    Assertions.assertFalse(ObjectUtils.isEmpty(csvContents));
    verify(calenderService, times(1)).getCarrierServiceCalender(anyString(), anyString());
    verify(transitService, times(1)).getTransitTimeEntries(anyString(), anyString());
    verify(carrierService, times(1)).getCarrierService(anyString());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSV() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(
            BaseResponse.builder()
                .payload(testUtil.getNodeCarrierServiceAndServiceOptionResponse(1))
                .build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    Assertions.assertNotNull(pojo);
    Assertions.assertNotNull(pojo.getFileContents());
    Assertions.assertTrue(pojo.getContentsLength() > 0);
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVEmptyPagePayloadData() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(null).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    Assertions.assertNotNull(pojo);
    Assertions.assertNotNull(pojo.getFileContents());
    Assertions.assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVNullResponse() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            any(), any(), any(), any(), any()))
        .thenReturn(null);

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(any(), any(), any(), any(), any());

    Assertions.assertNotNull(pojo);
    Assertions.assertNotNull(pojo.getFileContents());
    Assertions.assertEquals(0, pojo.getContentsLength());
  }

  @Test
  void downloadNodeCarrierServiceAndServiceOptionsDataCSVMultiplePages() throws IOException {
    ReflectionTestUtils.setField(csvDownloadUtilityService, "noOfRecordsPerPage", 200);
    NodeCarrierServiceAndServiceOptionResponse response1 =
        testUtil.getNodeCarrierServiceAndServiceOptionResponse();

    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            TestUtil.ORG_ID, null, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload(response1, 1)).build());

    when(dataUploadFeign.getListOfNodeCarrierServiceAndServiceOptionDetails(
            TestUtil.ORG_ID, 2, 200, null, null))
        .thenReturn(BaseResponse.builder().payload(responsePagePayload(response1, 2)).build());

    DownloadNodeCarrierServiceAndServiceOptionPojo pojo =
        csvDownloadUtilityService.downloadNodeCarrierServiceAndServiceOptionsDataCSV(
            TestUtil.ORG_ID);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(TestUtil.ORG_ID, null, 200, null, null);

    verify(dataUploadFeign, times(1))
        .getListOfNodeCarrierServiceAndServiceOptionDetails(TestUtil.ORG_ID, 2, 200, null, null);

    Assertions.assertNotNull(pojo);
    Assertions.assertNotNull(pojo.getFileContents());
    Assertions.assertTrue(pojo.getContentsLength() > 0);
  }

  private PagePayload<NodeCarrierServiceAndServiceOptionResponse> responsePagePayload(
      NodeCarrierServiceAndServiceOptionResponse response, int pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServicePagePayload =
        new PagePayload<>();

    response.setNodeId(TestUtil.NODE_ID + 1);
    response.setOrgId(TestUtil.ORG_ID);
    response.setStreet(TestUtil.STREET);
    response.setCity(TestUtil.CITY);
    response.setProvince(TestUtil.PROVINCE);
    response.setPostalCode(TestUtil.POSTAL_CODE);
    response.setCarrierServices(List.of(TestUtil.CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(TestUtil.SERVICE_OPTION));
    response.setActiveCombination(List.of(testUtil.getActiveCombination()));

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(2);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(response));

    return nodeCarrierServicePagePayload;
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

  @Test
  void downloadProcessingTimeBuffersByOrgIdWithPartialEmptyValuesAndNullValuesTest()
      throws IOException {
    List<ProcessingTimeBufferResponse> responseList =
        List.of(testUtil.getProcessingTimeBufferResponsePartialEmptyValues(TestUtil.NODE_ID));
    when(processingTimeBuffersService.getProcessingTimeBuffers(any())).thenReturn(responseList);

    File file = csvDownloadUtilityService.downloadProcessingTimeBuffersByOrgId(TestUtil.ORG_ID);

    Assertions.assertNotNull(file);
    verify(processingTimeBuffersService, times(1)).getProcessingTimeBuffers(any());
  }

  @Test
  void downloadNodesByOrgIdTest() throws IOException {
    List<NodeDto> nodeDtoList =
        List.of(testUtil.getNodeDto(TestUtil.NODE_ID), testUtil.getNodeDto(TestUtil.NODE_ID_2));
    List<NodeCarrierResponse> nodeCarrierResponses = testUtil.getNodeCarrierResponseList();
    List<NodeCalendarResponse> nodeCalendarResponses = testUtil.getNodeCalendarResponseList();

    when(nodeService.getNodeList(any())).thenReturn(nodeDtoList);
    when(calenderService.getNodeCalendar(any(), any())).thenReturn(nodeCalendarResponses);
    when(nodeCarrierService.getNodeCarrierResponse(any(), any())).thenReturn(nodeCarrierResponses);

    File file = csvDownloadUtilityService.downloadNodesByOrgId(TestUtil.ORG_ID);

    Assertions.assertNotNull(file);
    verify(nodeService, times(1)).getNodeList(any());
    verify(nodeCarrierService, times(2)).getNodeCarrierResponse(any(), any());
    verify(calenderService, times(2)).getNodeCalendar(any(), any());
  }

  @Test
  void downloadNodesByOrgIdEmptyValuesTest() throws IOException {
    List<NodeDto> nodeDtoList =
        List.of(testUtil.getNodeDto(TestUtil.NODE_ID), testUtil.getNodeDto(TestUtil.NODE_ID_2));

    when(nodeService.getNodeList(any())).thenReturn(nodeDtoList);
    when(calenderService.getNodeCalendar(any(), any())).thenReturn(new ArrayList<>());
    when(nodeCarrierService.getNodeCarrierResponse(any(), any())).thenReturn(new ArrayList<>());

    File file = csvDownloadUtilityService.downloadNodesByOrgId(TestUtil.ORG_ID);

    Assertions.assertNotNull(file);
    verify(nodeService, times(1)).getNodeList(any());
    verify(nodeCarrierService, times(2)).getNodeCarrierResponse(any(), any());
    verify(calenderService, times(2)).getNodeCalendar(any(), any());
  }

  @Test
  void downloadTransitBufferDetailsTest() {
    PreSignedUrlResponse preSignedUrlResponse = testUtil.getPreSignedUrlResponse();
    when(transitBufferFeign.getTransitBufferDetails(anyLong(), anyString()))
        .thenReturn(BaseResponse.builder().payload(preSignedUrlResponse).build());

    PreSignedUrlResponse response =
        csvDownloadUtilityService.downloadTransitBufferDetails(1L, "user1");

    Assertions.assertEquals(preSignedUrlResponse, response);
    verify(transitBufferFeign, times(1)).getTransitBufferDetails(anyLong(), anyString());
  }
}
