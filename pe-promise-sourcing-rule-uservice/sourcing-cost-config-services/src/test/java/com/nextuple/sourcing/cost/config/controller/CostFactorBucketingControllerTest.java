package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.service.CostFactorBucketTypeServiceImpl;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CostFactorBucketingControllerTest {
  @InjectMocks CostFactorBucketingController controller;
  @InjectMocks TestUtil testUtil;

  @Mock CostFactorBucketTypeServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create a cost factor bucket type")
  void createCostFactorBucketType() throws CommonServiceException {
    CostFactorBucketTypeDto dto = testUtil.getCostFactorBucketTypeResponse();
    when(service.createCostFactorBucketType(any(), any()))
        .thenReturn(testUtil.getCostFactorBucketTypeResponse());
    ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> response =
        controller.createCostFactorBucketType(ORG_ID, testUtil.getCostFactorBucketTypeRequest());

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).createCostFactorBucketType(anyString(), any());
  }

  @Test
  @DisplayName("Get a cost factor bucket type")
  void getCostFactorBucketType() throws CommonServiceException {
    CostFactorBucketTypeDto dto = testUtil.getCostFactorBucketTypeResponse();
    when(service.getCostFactorBucketType(any(), any()))
        .thenReturn(testUtil.getCostFactorBucketTypeResponse());
    ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> response =
        controller.getCostFactorBucket(ORG_ID, COST_FACTOR);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).getCostFactorBucketType(anyString(), any());
  }

  @Test
  @DisplayName("Update a cost factor bucket type")
  void updateCostFactorBucketType() throws CommonServiceException {
    CostFactorBucketTypeDto dto = testUtil.getCostFactorBucketTypeResponse();
    when(service.updateCostFactorBucketType(any(), any(), any()))
        .thenReturn(testUtil.getCostFactorBucketTypeResponse());
    ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> response =
        controller.updateCostFactorBucket(
            ORG_ID, COST_FACTOR, testUtil.updateCostFactorBucketTypeRequest());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).updateCostFactorBucketType(anyString(), any(), any());
  }

  @Test
  @DisplayName("Delete a cost factor bucket type")
  void deleteCostFactorBucketType() throws CommonServiceException {
    CostFactorBucketTypeDto dto = testUtil.getCostFactorBucketTypeResponse();
    when(service.deleteCostFactorBucketType(any(), any()))
        .thenReturn(testUtil.getCostFactorBucketTypeResponse());
    ResponseEntity<BaseResponse<CostFactorBucketTypeDto>> response =
        controller.deleteCostFactorBucket(ORG_ID, COST_FACTOR);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).deleteCostFactorBucketType(anyString(), any());
  }

  @Test
  void getCostFactorBucketTypeCacheKeysTest() {
    List<CostFactorBucketTypeCacheKeyDto> costFactorBucketTypeCacheKeyDtoList =
        testUtil.getCostFactorBucketTypeCacheKeysDtoList();

    when(service.getCostFactorBucketTypeCacheKeys(any()))
        .thenReturn(costFactorBucketTypeCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostFactorBucketTypeCacheKeyDto>>> response =
        controller.getCostFactorBucketTypeCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(costFactorBucketTypeCacheKeyDtoList, response.getBody().getPayload());

    verify(service, times(1)).getCostFactorBucketTypeCacheKeys(any());
  }
}
