package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorDiscreteBucketDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorDiscreteBucketRequest;
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
import org.springframework.http.HttpStatus;

class CostFactorDiscreteBucketServiceImplTest {
  @Mock private CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  @Mock private CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private BucketValidationService bucketValidationService;
  @Mock private CostFactorDiscreteBucketServiceImpl costFactorDiscreteBucketService;

  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "Bucket type not found for orgId and Cost Factor combination.";
  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private CostFactorDiscreteBucketServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost factor Discrete bucket - Happy path")
  void createCostFactorDiscreteBucket() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    CostFactorDiscreteBucketDto dto = service.createCostFactorDiscreteBucket(ORG_ID, request);

    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorDiscreteBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Create cost factor Discrete bucket - Bucket not found")
  void createCostFactorDiscreteBucketNotFound() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateOrgIdAndCostFactor(any(), any());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorDiscreteBucket(ORG_ID, request));

    Assertions.assertEquals(
        "Bucket type not found for orgId and Cost Factor combination.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, commonServiceException.getHttpStatus());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor Discrete bucket - New record already present")
  void createCostFactorDiscreteBucketDuplicateEntry() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorDiscreteBucket(ORG_ID, request));

    Assertions.assertEquals(
        "OrgId, Cost Factor and notation combination should be unique.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.CONFLICT, commonServiceException.getHttpStatus());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor Discrete bucket - Cost itinerary CREATED")
  void createCostFactorDiscreteBucketItineraryCREATED() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());

    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorDiscreteBucket(ORG_ID, request));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, commonServiceException.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor Discrete bucket - Wrong bucket type")
  void createCostFactorDiscreteBucketWrongBucketType() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorDiscreteBucket(ORG_ID, request));
    Assertions.assertEquals(
        "Bucket type of cost factor should be DISCRETE", commonServiceException.getMessage());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorDiscreteBucketRepository, times(0))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Get cost factor Discrete bucket - Happy Path")
  void getCostFactorDiscreteBucket() throws CommonServiceException {
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorDiscreteBucketEntity()));
    List<CostFactorDiscreteBucketDto> dto =
        service.getCostFactorDiscreteBucket(ORG_ID, COST_FACTOR);

    Assertions.assertEquals(ORG_ID, dto.get(0).getOrgId());
    verify(costFactorDiscreteBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Get cost factor Discrete bucket - Not found")
  void getCostFactorDiscreteBucketNotFound() throws CommonServiceException {
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Collections.emptyList());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.getCostFactorDiscreteBucket(ORG_ID, COST_FACTOR));

    Assertions.assertEquals(
        "Buckets not found for orgId, Cost Factor combination.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, commonServiceException.getHttpStatus());
    verify(costFactorDiscreteBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Update cost factor Discrete bucket - Happy path")
  void updateCostFactorDiscreteBucket() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    CostFactorDiscreteBucketDto dto = service.updateCostFactorDiscreteBucket(ID, ORG_ID, request);

    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Update cost factor Discrete bucket - Bucket not found")
  void updateCostFactorDiscreteBucketNotFound() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorDiscreteBucket(ID, ORG_ID, request));

    Assertions.assertEquals("Bucket not found for given id and orgId.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(0)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor Discrete bucket - bucket type not found")
  void updateCostFactorDiscreteBucketDuplicateEntry() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());

    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorDiscreteBucket(ID, ORG_ID, request));

    Assertions.assertEquals(
        "Bucket type not found for orgId and Cost Factor combination.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor Discrete bucket - Cost itinerary CREATED")
  void updateCostFactorDiscreteBucketItineraryCREATED() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());

    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorDiscreteBucket(ID, ORG_ID, request));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor Discrete bucket - Wrong bucket type")
  void updateCostFactorDiscreteBucketWrongBucketType() throws CommonServiceException {
    CostFactorDiscreteBucketRequest request = testUtil.getCostFactorDiscreteBucketRequest();
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorDiscreteBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorDiscreteBucket(ID, ORG_ID, request));
    Assertions.assertEquals(
        "Bucket type of cost factor should be DISCRETE", commonServiceException.getMessage());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Delete cost factor Discrete bucket - Happy path")
  void deleteCostFactorDiscreteBucket() throws CommonServiceException {
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostItineraryAndFactorsEntity()));
    doNothing().when(costFactorDiscreteBucketRepository).delete(any());

    CostFactorDiscreteBucketDto dto = service.deleteCostFactorDiscreteBucket(ID, ORG_ID);
    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("Delete cost factor Discrete bucket - Bucket not found")
  void deleteCostFactorDiscreteBucketNotFound() throws CommonServiceException {
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorDiscreteBucketRepository).delete(any());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> service.deleteCostFactorDiscreteBucket(ID, ORG_ID));

    Assertions.assertEquals("Bucket not found for given id and orgId.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).delete(any());
  }

  @Test
  @DisplayName("Delete cost factor Discrete bucket - Cost Itinerary CREATED")
  void deleteCostFactorDiscreteBucketNotFoundItineraryCREATED() throws CommonServiceException {
    when(costFactorDiscreteBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorDiscreteBucketEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorDiscreteBucketRepository).delete(any());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class, () -> service.deleteCostFactorDiscreteBucket(ID, ORG_ID));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());

    verify(costFactorDiscreteBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).delete(any());
  }

  @Test
  void getAllCostFactorDiscreteBucketCacheKeysTest() {
    List<CostFactorDiscreteBucketEntity> costFactorDiscreteBucketEntities =
        testUtil.getCostFactorDiscreteBucketEntityList();

    when(costFactorDiscreteBucketRepository.findAllCostFactorDiscreteBucketEntities(any()))
        .thenReturn(costFactorDiscreteBucketEntities);

    List<CostFactorDiscreteBucketCacheKeyDto> response =
        service.getCostFactorDiscreteBucketCacheKeys(2);

    Assertions.assertEquals(3, response.size());
    Assertions.assertEquals(
        costFactorDiscreteBucketEntities.get(0).getOrgId(), response.get(0).getOrgId());
  }
}
