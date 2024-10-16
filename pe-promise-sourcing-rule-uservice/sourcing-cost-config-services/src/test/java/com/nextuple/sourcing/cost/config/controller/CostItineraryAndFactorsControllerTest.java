package com.nextuple.sourcing.cost.config.controller;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ITINERARY_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_ITINERARY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.inbound.CostItineraryAndFactorsRequest;
import com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CostItineraryAndFactorsControllerTest {
  @Mock private CostItineraryAndFactorsService costItineraryAndFactorsService;

  @InjectMocks private CostItineraryAndFactorsController costItineraryAndFactorsController;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCostItineraryAndFactorsTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.createCostItineraryAndFactors(
            anyString(), any(CostItineraryAndFactorsRequest.class)))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.createCostItineraryAndFactors(
            ORG_ID, testUtil.getUpsertCostItineraryAndFactorsRequest());

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .createCostItineraryAndFactors(anyString(), any(CostItineraryAndFactorsRequest.class));
  }

  @Test
  void getCostItineraryAndFactorsByOrgIdAndIdTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.findCostItineraryAndFactorsByOrgIdAndId(
            anyString(), anyLong()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.getCostItineraryAndFactorsByOrgIdAndId(
            ORG_ID, COST_FACTOR_ITINERARY_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .findCostItineraryAndFactorsByOrgIdAndId(anyString(), anyLong());
  }

  @Test
  @DisplayName("Test for getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive")
  void getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveTest()
      throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService
            .findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                anyString(), anyString(), any(), anyBoolean()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController
            .getCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                ORG_ID, COST_ITINERARY, ItineraryStatusEnum.CREATED, true);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
            anyString(), anyString(), any(), anyBoolean());
  }

  @Test
  void updateCostItineraryAndFactorsTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.updateCostItineraryAndFactors(
            anyLong(), anyString(), any(CostItineraryAndFactorsRequest.class)))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.updateCostItineraryAndFactors(
            ORG_ID, COST_FACTOR_ID, testUtil.getUpsertCostItineraryAndFactorsRequest());

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .updateCostItineraryAndFactors(
            anyLong(), anyString(), any(CostItineraryAndFactorsRequest.class));
  }

  @Test
  void updateCostItineraryAndFactorsStatusTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(anyLong(), anyString()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.updateCostItineraryAndFactorsStatus(
            ORG_ID, COST_FACTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .updateCostItineraryAndFactorsStatus(anyLong(), anyString());
  }

  @Test
  void updateCostItineraryAndFactorsStatusByCostItineraryTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.updateCostItineraryAndFactorsStatusByCostItinerary(
            anyString(), anyString()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.updateCostItineraryAndFactorsStatusByCostItinerary(
            ORG_ID, COST_ITINERARY);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getCostItinerary(),
        Objects.requireNonNull(responseEntity.getBody()).getPayload().getCostItinerary());

    verify(costItineraryAndFactorsService, times(1))
        .updateCostItineraryAndFactorsStatusByCostItinerary(anyString(), anyString());
  }

  @Test
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryTest()
      throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.updateCostItineraryAndFactorsActiveStatusByCostItinerary(
            anyString(), anyString(), anyBoolean()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.updateCostItineraryAndFactorsActiveStatusByCostItinerary(
            ORG_ID, COST_ITINERARY, true);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getCostItinerary(),
        Objects.requireNonNull(responseEntity.getBody()).getPayload().getCostItinerary());

    verify(costItineraryAndFactorsService, times(1))
        .updateCostItineraryAndFactorsActiveStatusByCostItinerary(
            anyString(), anyString(), anyBoolean());
  }

  @Test
  void deleteCostItineraryAndFactorsTest() throws CommonServiceException {
    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        testUtil.getCostItineraryAndFactorsResponse();
    when(costItineraryAndFactorsService.deleteCostItineraryAndFactors(anyString(), anyLong()))
        .thenReturn(costItineraryAndFactorsResponse);

    ResponseEntity<BaseResponse<CostItineraryAndFactorsDto>> responseEntity =
        costItineraryAndFactorsController.deleteCostItineraryAndFactors(ORG_ID, COST_FACTOR_ID);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(
        costItineraryAndFactorsResponse.getId(), responseEntity.getBody().getPayload().getId());

    verify(costItineraryAndFactorsService, times(1))
        .deleteCostItineraryAndFactors(anyString(), anyLong());
  }

  @Test
  void getCostItineraryAndFactorsCacheKeysTest() {
    List<CostItineraryAndFactorsCacheKeyDto> costItineraryAndFactorsCacheKeyDtoList =
        testUtil.getCostItineraryAndFactorsCacheKeyDtoList();

    when(costItineraryAndFactorsService.getCostItineraryAndFactorsCacheKeys(any()))
        .thenReturn(costItineraryAndFactorsCacheKeyDtoList);

    ResponseEntity<BaseResponse<List<CostItineraryAndFactorsCacheKeyDto>>> response =
        costItineraryAndFactorsController.getCostItineraryAndFactorsCacheKeys(2);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        costItineraryAndFactorsCacheKeyDtoList, response.getBody().getPayload());

    verify(costItineraryAndFactorsService, times(1)).getCostItineraryAndFactorsCacheKeys(any());
  }
}
