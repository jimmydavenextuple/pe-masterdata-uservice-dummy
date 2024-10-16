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
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.inbound.CostFactorDiscreteBucketRequest;
import com.nextuple.sourcing.cost.config.service.CostFactorDiscreteBucketServiceImpl;
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

class CostFactorDiscreteBucketControllerTest {
  @InjectMocks CostFactorDiscreteBucketController controller;
  @InjectMocks TestUtil testUtil;
  @Mock CostFactorDiscreteBucketServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create a cost factor discrete bucket")
  void createCostFactorContiguousBucket() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    CostFactorDiscreteBucketDto dto = testUtil.getCostFactorDiscreteBucketDto();
    when(service.createCostFactorDiscreteBucket(ORG_ID, request)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> response =
        controller.createCostFactorDiscreteBucket(ORG_ID, request);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).createCostFactorDiscreteBucket(anyString(), any());
  }

  @Test
  @DisplayName("Get a cost factor Discrete bucket")
  void getCostFactorDiscreteBucket() throws CommonServiceException {
    CostFactorDiscreteBucketDto dto = testUtil.getCostFactorDiscreteBucketDto();
    when(service.getCostFactorDiscreteBucket(ORG_ID, COST_FACTOR)).thenReturn(List.of(dto));

    ResponseEntity<BaseResponse<List<CostFactorDiscreteBucketDto>>> response =
        controller.getCostFactorDiscreteBucket(ORG_ID, COST_FACTOR);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().get(0).getId());
    verify(service, times(1)).getCostFactorDiscreteBucket(any(), any());
  }

  @Test
  @DisplayName("Update a cost factor Discrete bucket")
  void updateCostFactorBucket() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    CostFactorDiscreteBucketDto dto = testUtil.getCostFactorDiscreteBucketDto();
    when(service.updateCostFactorDiscreteBucket(ID, ORG_ID, request)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> response =
        controller.updateCostFactorBucket(ORG_ID, ID, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).updateCostFactorDiscreteBucket(any(), anyString(), any());
  }

  @Test
  @DisplayName("Delete a cost factor Discrete bucket")
  void deleteCostFactorBucket() throws CommonServiceException {
    CostFactorDiscreteBucketDto dto = testUtil.getCostFactorDiscreteBucketDto();
    when(service.deleteCostFactorDiscreteBucket(ID, ORG_ID)).thenReturn(dto);

    ResponseEntity<BaseResponse<CostFactorDiscreteBucketDto>> response =
        controller.deleteCostFactorBucket(ORG_ID, ID);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(dto.getId(), response.getBody().getPayload().getId());
    verify(service, times(1)).deleteCostFactorDiscreteBucket(any(), anyString());
  }

  @Test
  void getCostFactorDiscreteBucketCacheKeysTest() {
    List<CostFactorDiscreteBucketCacheKeyDto> costFactorDiscreteBucketCacheKeyDtoList =
        testUtil.getCostFactorDiscreteBucketCacheKeysDtoList();

    when(service.getCostFactorDiscreteBucketCacheKeys(any()))
        .thenReturn(costFactorDiscreteBucketCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostFactorDiscreteBucketCacheKeyDto>>> response =
        controller.getCostFactorDiscreteBucketCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        costFactorDiscreteBucketCacheKeyDtoList, response.getBody().getPayload());

    verify(service, times(1)).getCostFactorDiscreteBucketCacheKeys(any());
  }
}
