package com.nextuple.vendor.consumer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.enums.ActionEnum;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.util.DateUtil;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.master.data.integration.service.ErrorHandlingService;
import com.nextuple.vendor.consumer.TestUtil;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.domain.feign.VendorFeign;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
class VendorBatchServiceImplTest {

  @InjectMocks private VendorBatchServiceImpl vendorBatchService;
  @InjectMocks private TestUtil testUtil;
  @Mock private VendorFeign vendorFeign;
  @Mock private ErrorHandlingService errorHandlingService;
  @Mock private VendorPersistenceService vendorPersistenceService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);

    ReflectionTestUtils.setField(vendorBatchService, "errorHandlingService", errorHandlingService);
  }

  @Test
  @DisplayName("When vendor feed with create action is processed successfully")
  void processBatchRecordsTestWhenActionIsCreate() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.CREATE));
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  void processBatchRecordsTestWithNoRetry() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.CREATE));
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor created successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor created successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  void processBatchRecordsTestWhenActionIsUpdate() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.UPDATE));
    Mockito.when(vendorFeign.updateVendorDetails(any(), any(), any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor details updated successfully"));
    ResponseDto responseDto =
        testUtil.createResponseDto(1, 200, "Vendor details updated successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor details updated successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).updateVendorDetails(any(), any(), any());
  }

  @Test
  void processBatchRecordsTestWhenActionIsDelete() {
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests =
        List.of(testUtil.getVendorFeedRequest(ActionEnum.DELETE));
    Mockito.when(vendorFeign.deleteVendor(any(), any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor deleted successfully"));
    ResponseDto responseDto = testUtil.createResponseDto(1, 200, "Vendor deleted successfully");
    responseDto.setMessage(
        testUtil.getBaseResponseOfVendorFeed("Vendor deleted successfully").getMessage());
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 1, 0);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleSuccessResponse(any(), any(), any(), any());
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(1)).deleteVendor(any(), any());
  }

  @Test
  void processBatchRecordsTestForOutdatedRecords()
      throws CommonServiceException, VendorDomainException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    List<BatchRequest<VendorFeedDto>> vendorFeedRequests = List.of(batchRequest);
    ResponseDto responseDto =
        testUtil.createResponseDto(
            1, HttpStatus.BAD_REQUEST.value(), "Can't process the record as it's outdated");
    List<ResponseDto> responseDtoList = List.of(responseDto);
    BatchResponse batchResponse = testUtil.getVendorBatchResponse(1, 0, 1);
    batchResponse.setResponses(responseDtoList);
    Mockito.doNothing()
        .when(errorHandlingService)
        .handleExceptions(anyInt(), any(), any(), any(), any(), any());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    Assertions.assertEquals(
        batchResponse, vendorBatchService.processRecordsWithRetry(vendorFeedRequests));
    verify(vendorFeign, times(0)).createVendor(any());
  }

  @Test
  void handleNodeRetryTest() {
    BatchRequest<?> inputFeedRequest = testUtil.getVendorFeedRequest(ActionEnum.CREATE);
    Mockito.when(vendorFeign.createVendor(any()))
        .thenReturn(testUtil.getBaseResponseOfVendorFeed("Vendor created successfully"));
    String responseMessage = vendorBatchService.handleRetry(inputFeedRequest);

    Assertions.assertEquals("Vendor created successfully", responseMessage);

    verify(vendorFeign, times(1)).createVendor(any());
  }

  @Test
  void checkForOutdatedRecordTestWhenRecordIsOutdated() throws VendorDomainException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), 1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> vendorBatchService.checkForOutdatedRecord(batchRequest));
    Assertions.assertEquals("Can't process the record as it's outdated", exception.getMessage());
  }

  @Test
  void checkForOutdatedRecordTestWhenRecordIsNotOutdated() throws VendorDomainException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto();
    vendorDomainDto.setLastModifiedDate(DateUtil.addDaysToDate(new Date(), -1));
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.of(vendorDomainDto));
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  void checkForOutdatedRecordTestWhenVendorDoesNotExist() throws VendorDomainException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenReturn(Optional.empty());
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }

  @Test
  void checkForOutdatedRecordTestWhenVendorDomainExceptionIsThrown() throws VendorDomainException {
    BatchRequest<VendorFeedDto> batchRequest = testUtil.getVendorFeedRequest(ActionEnum.UPDATE);
    batchRequest.setReceivedTimestamp(new Date());
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(anyString(), anyString()))
        .thenThrow(
            new VendorDomainException(
                "Vendor not found",
                batchRequest.getPayload().getVendorId(),
                batchRequest.getPayload().getOrgId()));
    Assertions.assertDoesNotThrow(() -> vendorBatchService.checkForOutdatedRecord(batchRequest));
  }
}
