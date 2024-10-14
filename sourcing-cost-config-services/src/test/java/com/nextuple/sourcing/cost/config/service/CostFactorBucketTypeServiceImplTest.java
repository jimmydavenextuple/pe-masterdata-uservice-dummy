package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorBucketTypeDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.http.HttpStatus;

class CostFactorBucketTypeServiceImplTest {
  @InjectMocks private CostFactorBucketTypeServiceImpl costFactorBucketTypeServiceImpl;
  @InjectMocks private TestUtil testUtil;
  @Mock private CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  @Mock private CostFactorRepository costFactorRepository;
  @Mock private BucketValidationService bucketValidationService;

  @Mock private CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  @Mock private CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;

  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost factor bucket type - Happy path")
  void createCostFactorBucketType() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CostFactorBucketTypeDto response =
        costFactorBucketTypeServiceImpl.createCostFactorBucketType(
            ORG_ID, testUtil.getCostFactorBucketTypeRequest());
    Assertions.assertEquals(ORG_ID, response.getOrgId());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Create cost factor bucket type - Cost Factor not found")
  void createCostFactorBucketTypeCostFactorMissing() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Collections.emptyList());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());

    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.createCostFactorBucketType(
                    ORG_ID, testUtil.getCostFactorBucketTypeRequest()));
    Assertions.assertEquals("Cost Factor not found.", response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Create cost factor bucket type - Cost Factor orgId combination already present")
  void createCostFactorBucketTypeDuplicate() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.createCostFactorBucketType(
                    ORG_ID, testUtil.getCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "OrgId and Cost Factor combination should be unique.", response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName(
      "Create cost factor bucket type - When rateCardLookUp is disabled then do not create bucket types")
  void createCostFactorBucketTypeIsRateCardLookUpFalse() throws CommonServiceException {
    var costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsRateCardLookUpRequired(false);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactorEntity));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.createCostFactorBucketType(
                    ORG_ID, testUtil.getCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "Cost factor bucket type cannot be created as rate card lookup is disabled for cost factor.",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor bucket type - CostItinerary in CREATED state")
  void createCostFactorBucketTypeItineraryCREATED() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.createCostFactorBucketType(
                    ORG_ID, testUtil.getCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Get Cost factor bucketing type")
  void getCostFactorBucketType() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    CostFactorBucketTypeDto dto =
        costFactorBucketTypeServiceImpl.getCostFactorBucketType(ORG_ID, COST_FACTOR);
    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Get Cost factor bucketing type - Not found")
  void getCostFactorBucketTypeNotFound() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> costFactorBucketTypeServiceImpl.getCostFactorBucketType(ORG_ID, COST_FACTOR));
    Assertions.assertEquals(
        "OrgId and Cost Factor combination not found.", commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, commonServiceException.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Update existing DISCRETE cost factor bucket type - Happy path")
  void updateExistingDiscreteCostFactorBucketType() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    CostFactorBucketTypeDto response =
        costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
            ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest());
    Assertions.assertEquals(ORG_ID, response.getOrgId());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Update existing CONTIGUOUS cost factor bucket type - Happy path")
  void updateExistingContiguousCostFactorBucketType() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(List.of());
    CostFactorBucketTypeDto response =
        costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
            ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest());
    Assertions.assertEquals(ORG_ID, response.getOrgId());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Update existing DISCRETE cost factor bucket type - Exception Scenario")
  void updateExistingDiscreteCostFactorBucketTypeException() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntityList());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
                    ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "Can't update bucket type as values are assigned to existing bucket type",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update existing CONTIGUOUS cost factor bucket type - Exception Scenario")
  void updateExistingContiguousCostFactorBucketTypeException() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntityList());
    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
                    ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "Can't update bucket type as values are assigned to existing bucket type",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor bucket type - Cost Factor not found")
  void updateCostFactorBucketTypeCostFactorMissing() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Collections.emptyList());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
                    ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest()));
    Assertions.assertEquals("Cost Factor not found.", response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Update cost factor bucket type - Cost Factor orgId combination not found")
  void updateCostFactorBucketTypeCostFactorOrgIdCombinationNotFound()
      throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
                    ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest()));
    Assertions.assertEquals("OrgId and Cost Factor combination not found.", response.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Update cost factor bucket type - CostItinerary in CREATED state")
  void updateCostFactorBucketTypeItineraryCREATED() throws CommonServiceException {
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.save(any()))
        .thenReturn(testUtil.getCostFactorBucketTypeEntity());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costFactorBucketTypeServiceImpl.updateCostFactorBucketType(
                    ORG_ID, COST_FACTOR, testUtil.getUpdateCostFactorBucketTypeRequest()));
    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Delete cost factor bucket type - Happy path")
  void deleteCostFactorBucketType() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorBucketTypeRepository).delete(any());

    CostFactorBucketTypeDto response =
        costFactorBucketTypeServiceImpl.deleteCostFactorBucketType(ORG_ID, COST_FACTOR);
    Assertions.assertEquals(ORG_ID, response.getOrgId());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Delete cost factor bucket type - Cost Factor orgId combination not found")
  void deleteCostFactorBucketTypeCostFactorOrgIdCombinationNotFound1()
      throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorBucketTypeRepository).delete(any());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> costFactorBucketTypeServiceImpl.deleteCostFactorBucketType(ORG_ID, COST_FACTOR));
    Assertions.assertEquals("OrgId and Cost Factor combination not found.", response.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
  }

  @Test
  @DisplayName("Delete cost factor bucket type - CostItinerary in CREATED state")
  void deleteCostFactorBucketTypeItineraryCREATED1() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorBucketTypeRepository).delete(any());

    CommonServiceException response =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> costFactorBucketTypeServiceImpl.deleteCostFactorBucketType(ORG_ID, COST_FACTOR));
    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        response.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, response.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
  }

  @Test
  void getAllCostFactorBucketTypeCacheKeysTest() {
    List<CostFactorBucketTypeEntity> costFactorBucketTypeEntities =
        testUtil.getCostFactorBucketTypeEntityList();

    when(costFactorBucketTypeRepository.findAllCostFactorBucketTypeEntities(any()))
        .thenReturn(costFactorBucketTypeEntities);

    List<CostFactorBucketTypeCacheKeyDto> response =
        costFactorBucketTypeServiceImpl.getCostFactorBucketTypeCacheKeys(2);

    Assertions.assertEquals(3, response.size());
    Assertions.assertEquals(
        costFactorBucketTypeEntities.get(0).getOrgId(), response.get(0).getOrgId());
    verify(costFactorBucketTypeRepository, VerificationModeFactory.times(1))
        .findAllCostFactorBucketTypeEntities(any());
  }
}
