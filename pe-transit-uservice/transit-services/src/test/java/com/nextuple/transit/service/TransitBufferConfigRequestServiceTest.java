/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.jobs.framework.common.clients.JobsDashboardClient;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import com.nextuple.jobs.framework.common.service.FileService;
import com.nextuple.transit.TestUtil;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.enums.TransitBufferReqJobRefEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.persistence.domain.TransitBufferConfigRequestDomainDto;
import com.nextuple.transit.persistence.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.persistence.service.impl.TransitBufferConfigRequestPersistenceServiceImpl;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferConfigRequestServiceTest {

  @InjectMocks private TransitBufferConfigRequestService transitBufferConfigRequestService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private TransitBufferConfigRequestPersistenceServiceImpl
      transitBufferConfigRequestPersistenceService;

  @Mock private FileService fileService;

  @Mock private JobsDashboardClient jobsDashboardClient;

  @Mock TransitBufferReqJobRefService transitBufferReqJobRefService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void processTransitBufferRequestTest1()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
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
        .thenReturn(testUtil.getTransitBufferReqJobRefResponse());
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(2))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest2() throws CommonServiceException, IOException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    Map<String, Collection<String>> headers = new HashMap<>();
    when(fileService.getFile(anyString(), anyString())).thenReturn(testUtil.getFileResponse());
    when(jobsDashboardClient.createFileMetadata(any(FileMetaDataCreationRequest.class)))
        .thenReturn(testUtil.getFileMetaDataResponse());
    doNothing().when(fileService).uploadFile(anyString(), anyString(), any());
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(any()))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    when(jobsDashboardClient.processJobOfflineWithFileMetaDataId(anyString(), any(), anyLong()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(Request.HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes(),
                headers));

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals("Upstream error : Failed to create job", exception.getMessage());
  }

  @Test
  @DisplayName("Updating the transit buffer, and checking the status")
  void processTransitBufferRequestTest3()
      throws CommonServiceException,
          TransitBufferReqJobRefDomainException,
          IOException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest("UPDATE");
    transitBufferConfigRequest.setTransitBufferRequestId(TestUtil.ID);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    transitBufferConfigRequestDomainDto.setFileMetaDataId(TestUtil.FILE_META_DATA_ID);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
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
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(3))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(2)).findById(anyLong());
  }

  @Test
  @DisplayName("Deleting the transit buffer, and checking the status")
  void processTransitBufferRequestTest4()
      throws CommonServiceException,
          TransitBufferReqJobRefDomainException,
          IOException,
          CsvException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest("D");
    transitBufferConfigRequest.setTransitBufferRequestId(TestUtil.ID);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    transitBufferConfigRequestDomainDto.setFileMetaDataId(TestUtil.FILE_META_DATA_ID);
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.of(transitBufferConfigRequestDomainDto));
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
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.deleteTransitBufferRequest(
            TestUtil.ID, TestUtil.CREATED_BY);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(3))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(3)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest5()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
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
        .thenReturn(testUtil.getTransitBufferReqJobRefResponse());
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(2))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void processTransitBufferRequestTest6()
      throws CommonServiceException,
          IOException,
          TransitBufferReqJobRefDomainException,
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
        .thenReturn(testUtil.getTransitBufferReqJobRefResponse());
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.CREATED));

    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));

    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(2))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
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
  void processTransitBufferRequestEmptySourceGeoZoneDataTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    FileResponse fileResponse =
        testUtil.getFileResponseWithPartialEmptyData(
            "sourceGeozone,destinationGeozone\n" + ",H1B\n");
    when(fileService.getFile(anyString(), anyString())).thenReturn(fileResponse);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Source geo zone or destination geo zone column is empty", exception.getMessage());
  }

  @Test
  void processTransitBufferRequestEmptyDestinationGeoZoneDataTest() throws CommonServiceException {
    TransitBufferConfigRequest transitBufferConfigRequest =
        testUtil.getTransitBufferConfigRequest(TestUtil.ACTION);
    FileResponse fileResponse =
        testUtil.getFileResponseWithPartialEmptyData(
            "sourceGeozone,destinationGeozone\n" + "H1B,\n");
    when(fileService.getFile(anyString(), anyString())).thenReturn(fileResponse);

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.processTransitBufferRequest(
                    transitBufferConfigRequest));
    Assertions.assertEquals(
        "Source geo zone or destination geo zone column is empty", exception.getMessage());
  }

  @Test
  void updateTransitBufferRequestStatusTest1() throws CommonServiceException {
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    when(transitBufferConfigRequestPersistenceService.saveTransitBufferConfigRequest(
            any(TransitBufferConfigRequestDomainDto.class)))
        .thenReturn(
            testUtil.getTransitBufferConfigRequestDomainDto(
                TransitBufferConfigRequestStatusEnum.INPROGRESS));
    TransitBufferConfigResponse transitBufferConfigResponse =
        transitBufferConfigRequestService.updateTransitBufferRequestStatus(
            TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigResponse(TransitBufferConfigRequestStatusEnum.INPROGRESS),
        transitBufferConfigResponse);
    verify(transitBufferConfigRequestPersistenceService, times(1))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void updateTransitBufferRequestStatusTest2() throws CommonServiceException {
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.updateTransitBufferRequestStatus(
                    TestUtil.ID, TransitBufferConfigRequestStatusEnum.INPROGRESS));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRequestPersistenceService, times(0))
        .saveTransitBufferConfigRequest(any(TransitBufferConfigRequestDomainDto.class));
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void fetchTransitBufferRequestsTest() throws CommonServiceException {
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto1 =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto2 =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    when(transitBufferConfigRequestPersistenceService.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenReturn(
            List.of(transitBufferConfigRequestDomainDto1, transitBufferConfigRequestDomainDto2));

    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse());
    List<TransitBufferConfigResponse> transitBufferConfigResponse =
        transitBufferConfigRequestService.fetchTransitBufferRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(2, transitBufferConfigResponse.size());
    verify(transitBufferConfigRequestPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void fetchTransitBufferRequestsTestErrorWhileFetchingMetaDetails() throws CommonServiceException {
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto1 =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.CREATED);
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto2 =
        testUtil.getTransitBufferConfigRequestDomainDto(
            TransitBufferConfigRequestStatusEnum.COMPLETED);
    when(transitBufferConfigRequestPersistenceService.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenReturn(
            List.of(transitBufferConfigRequestDomainDto1, transitBufferConfigRequestDomainDto2));

    when(jobsDashboardClient.findFileMetadataById(anyLong()))
        .thenReturn(testUtil.getFileMetaDataResponse())
        .thenThrow(new RuntimeException());
    List<TransitBufferConfigResponse> transitBufferConfigResponse =
        transitBufferConfigRequestService.fetchTransitBufferRequests(
            TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID);
    Assertions.assertEquals(1, transitBufferConfigResponse.size());
    verify(transitBufferConfigRequestPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void fetchTransitBufferRequestsTestException() throws CommonServiceException {
    when(transitBufferConfigRequestPersistenceService.findByOrgIdAndCarrierServiceId(
            anyString(), anyString(), any()))
        .thenThrow(new RuntimeException("Unable to fetch transit buffer requests"));
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                transitBufferConfigRequestService.fetchTransitBufferRequests(
                    TestUtil.ORG_ID, TestUtil.CARRIER_SERVICE_ID));

    Assertions.assertEquals("Unable to fetch transit buffer requests", exception.getMessage());
    verify(transitBufferConfigRequestPersistenceService, times(1))
        .findByOrgIdAndCarrierServiceId(anyString(), anyString(), any());
  }

  @Test
  void getTransitBufferRequestTest() throws CommonServiceException {
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(
            Optional.of(
                testUtil.getTransitBufferConfigRequestDomainDto(
                    TransitBufferConfigRequestStatusEnum.CREATED)));
    TransitBufferConfigRequestDomainDto transitBufferConfigRequestDomainDto =
        transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID);
    Assertions.assertEquals(
        testUtil.getTransitBufferConfigRequestDomainDto2(
            TransitBufferConfigRequestStatusEnum.CREATED),
        transitBufferConfigRequestDomainDto);
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }

  @Test
  void getTransitBufferRequestExceptionTest() throws CommonServiceException {
    when(transitBufferConfigRequestPersistenceService.findById(anyLong()))
        .thenReturn(Optional.empty());
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> transitBufferConfigRequestService.getTransitBufferRequest(TestUtil.ID));
    Assertions.assertEquals(
        "Transit buffer config request not found with given id", exception.getMessage());
    verify(transitBufferConfigRequestPersistenceService, times(1)).findById(anyLong());
  }
}
