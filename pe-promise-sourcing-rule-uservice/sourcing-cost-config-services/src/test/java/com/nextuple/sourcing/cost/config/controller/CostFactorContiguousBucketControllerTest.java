package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorContiguousBucketRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorContiguousBucketServiceImpl;
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

class CostFactorContiguousBucketControllerTest {
  @InjectMocks CostFactorContiguousBucketController controller;
  @InjectMocks TestUtil testUtil;

  @Mock CostFactorContiguousBucketServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create a cost factor contiguous bucket")
  void createCostFactorContiguousBucket() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    CostFactorContiguousBucketDto dto = testUtil.getCostFactorContiguousBucketDto();
    when(service.createCostFactorContiguousBucket(ORG_ID, request)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorContiguousBucketDto>> response =
        controller.createCostFactorContiguousBucket(ORG_ID, request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).createCostFactorContiguousBucket(anyString(), any());
  }

  @Test
  @DisplayName("Get a cost factor contiguous bucket")
  void getCostFactorDiscreteBucket() throws CommonServiceException {
    CostFactorContiguousBucketDto dto = testUtil.getCostFactorContiguousBucketDto();
    when(service.getCostFactorContiguousBuckets(ORG_ID, COST_FACTOR)).thenReturn(List.of(dto));

    ResponseEntity<BaseResponse<List<CostFactorContiguousBucketDto>>> response =
        controller.getCostFactorContiguousBucket(
            ORG_ID, testUtil.getGetCostFactorContiguousBucketRequest().getCostFactor());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().get(0).getId());
    verify(service, times(1)).getCostFactorContiguousBuckets(anyString(), any());
  }

  @Test
  @DisplayName("Update a cost factor contiguous bucket")
  void updateCostFactorBucket() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    CostFactorContiguousBucketDto dto = testUtil.getCostFactorContiguousBucketDto();
    when(service.updateCostFactorContiguousBucket(ID, ORG_ID, request)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorContiguousBucketDto>> response =
        controller.updateCostFactorBucket(ORG_ID, ID, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).updateCostFactorContiguousBucket(any(), anyString(), any());
  }

  @Test
  @DisplayName("Delete a cost factor contiguous bucket")
  void deleteCostFactorBucket() throws CommonServiceException {
    CostFactorContiguousBucketDto dto = testUtil.getCostFactorContiguousBucketDto();
    when(service.deleteCostFactorContiguousBucket(ID, ORG_ID)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorContiguousBucketDto>> response =
        controller.deleteCostFactorBucket(ORG_ID, ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).deleteCostFactorContiguousBucket(any(), anyString());
  }

  @Test
  void getCostFactorContiguousBucketCacheKeysTest() {
    List<CostFactorContiguousBucketCacheKeyDto> costFactorContiguousBucketCacheKeyDtoList =
        testUtil.getCostFactorContiguousBucketCacheKeysDtoList();

    when(service.getCostFactorContiguousBucketCacheKeys(any()))
        .thenReturn(costFactorContiguousBucketCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostFactorContiguousBucketCacheKeyDto>>> response =
        controller.getCostFactorContiguousBucketCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        costFactorContiguousBucketCacheKeyDtoList, response.getBody().getPayload());

    verify(service, times(1)).getCostFactorContiguousBucketCacheKeys(any());
  }
}
