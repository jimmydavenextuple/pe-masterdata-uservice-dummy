package com.hbc.transit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.hbc.jobs.framework.common.service.FileService;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitBufferConfigRequestEntity;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
import com.hbc.transit.repository.TransitBufferConfigRepository;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferConfigRequestServiceTest {

  @InjectMocks private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @InjectMocks private TestUtil testUtil;

  @Mock private TransitBufferConfigRepository transitBufferConfigRepository;

  @Mock private FileService fileService;

  @Mock private JobsDashboardClient jobsDashboardClient;

  @Mock TransitBufferReqJobRefService transitBufferReqJobRefService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processTransitBufferRequestTest1()
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenReturn(testUtil.getJobResponse());
    when(transitBufferReqJobRefService.createTransitBufferReqJobRef(
            any(TransitBufferReqJobRefRequest.class)))
        .thenReturn(testUtil.getTransBufferReqJobRefResponse());
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(2))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest2() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(transitBufferConfigRepository.save(any()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals("Upstream error : Failed to create job", exception.getMessage());
  }

  @Test
  void processTransitBufferRequestTest3()
      throws CommonServiceException, TransitBufferReqJobRefDomainException, IOException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest("U");
    transitBufferConfigRequest.setTransitBufferRequestId(TestUtil.ID);
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    transitBufferConfigRequestEntity.setFileMetaDataId(TestUtil.FILE_META_DATA_ID);
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenReturn(testUtil.getJobResponse());
    when(transitBufferReqJobRefService.createTransitBufferReqJobRef(
            any(TransitBufferReqJobRefRequest.class)))
        .thenReturn(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.UPDATE));
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(2))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(2)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest4()
      throws CommonServiceException, TransitBufferReqJobRefDomainException, IOException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest("D");
    transitBufferConfigRequest.setTransitBufferRequestId(TestUtil.ID);
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    transitBufferConfigRequestEntity.setFileMetaDataId(TestUtil.FILE_META_DATA_ID);
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestEntity));
    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse());
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenReturn(testUtil.getJobResponse());
    when(transitBufferReqJobRefService.createTransitBufferReqJobRef(
            any(TransitBufferReqJobRefRequest.class)))
        .thenReturn(testUtil.getTransBufferReqJobRefResponse1(TransitBufferReqJobRefEnum.DELETE));
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.deleteTransitBufferRequest(
            TestUtil.ID, TestUtil.CREATED_BY);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(2))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(3)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest5()
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse2());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenReturn(testUtil.getJobResponse());
    when(transitBufferReqJobRefService.createTransitBufferReqJobRef(
            any(TransitBufferReqJobRefRequest.class)))
        .thenReturn(testUtil.getTransBufferReqJobRefResponse());
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(2))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest6()
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse3());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenReturn(testUtil.getJobResponse());
    when(transitBufferReqJobRefService.createTransitBufferReqJobRef(
            any(TransitBufferReqJobRefRequest.class)))
        .thenReturn(testUtil.getTransBufferReqJobRefResponse());
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(2))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestInvalidActionTest() {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(null);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "No action type specified / Invalid action type", exception.getMessage());
  }

  @Test
  void processTransitBufferRequestEmptyFileTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    FileResponse fileResponse = testUtil.getFileResponseWithEmptyCSVFile();
    when(fileService.getFile(anyString(), anyString())).thenReturn(fileResponse);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals("No Records found in the csv", exception.getMessage());
  }

  @Test
  void processTransitBufferRequestInvalidHeadersTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    FileResponse fileResponse = testUtil.getFileResponseWithInvalidHeaders();
    when(fileService.getFile(anyString(), anyString())).thenReturn(fileResponse);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Transit buffer data uploaded file has invalid headers.", exception.getMessage());
  }

  @Test
  void updateTransitBufferRequestStatusTest1() throws CommonServiceException {
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    when(transitBufferConfigRepository.save(any(TransitBufferConfigRequestEntity.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestEntity(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        transitBufferConfigResponse);
    verify(transitBufferConfigRepository, times(1))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void updateTransitBufferRequestStatusTest2() {
    when(transitBufferConfigRepository.findById(anyLong())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                    TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRepository, times(0))
        .save(any(TransitBufferConfigRequestEntity.class));
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void fetchTransitBufferRequestsTest() throws CommonServiceException {
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity1 =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity2 =
        testUtil.getTransitBufferConfigRequestEntity(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    when(transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenReturn(List.of(transitBufferConfigRequestEntity1, transitBufferConfigRequestEntity2));

    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse());
    List<TransitBufferConfigResponse> transitBufferConfigResponse =
        transitBufferConfigRequestService.fetchTransitBufferRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(2, transitBufferConfigResponse.size());
    verify(transitBufferConfigRepository, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void fetchTransitBufferRequestsTestErrorWhileFetchingMetaDetails() throws CommonServiceException {
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity1 =
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED);
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity2 =
        testUtil.getTransitBufferConfigRequestEntity(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    when(transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenReturn(List.of(transitBufferConfigRequestEntity1, transitBufferConfigRequestEntity2));

    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse())
        .thenThrow(new RuntimeException());
    List<TransitBufferConfigResponse> transitBufferConfigResponse =
        transitBufferConfigRequestService.fetchTransitBufferRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(1, transitBufferConfigResponse.size());
    verify(transitBufferConfigRepository, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void fetchTransitBufferRequestsTestException() {
    when(transitBufferConfigRepository.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("Unable to fetch transit buffer requests"));
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.fetchTransitBufferRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Unable to fetch transit buffer requests", exception.getMessage());
    verify(transitBufferConfigRepository, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void getTransitBufferRequestTest() throws CommonServiceException {
    when(transitBufferConfigRepository.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestEntity(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    TransitBufferConfigRequestEntity transitBufferConfigRequestEntity =
        transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigRequestEntity(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigRequestEntity);
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }

  @Test
  void getTransitBufferRequestExceptionTest() {
    when(transitBufferConfigRepository.findById(anyLong())).thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRepository, times(1)).findById(anyLong());
  }
}
