package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorContiguousBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.ArrayList;
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

class BucketValidationServiceTest {
  @Mock private CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  @Mock private CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  @Mock private CostFactorRepository costFactorRepository;
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private BucketValidationService bucketValidationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("OrgId and cost factor combination present")
  void validateOrgIdAndCostFactorTest() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateOrgIdAndCostFactor(ORG_ID, COST_FACTOR));
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("OrgId and cost factor combination not present")
  void validateOrgIdAndCostFactorExceptionTest() throws CommonServiceException {
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> bucketValidationService.validateOrgIdAndCostFactor(ORG_ID, COST_FACTOR));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Cost itinerary in DRAFT status")
  void validateCostItineraryStatusTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostItineraryAndFactorsEntity()));
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateCostItineraryStatus(ORG_ID, COST_FACTOR));
    verify(costItineraryAndFactorsRepository, times(1))
        .findCostItinerariesByCostFactor(any(), any());
  }

  @Test
  @DisplayName("Cost itinerary in CREATED status")
  void validateCostItineraryStatusExceptionTest() throws CommonServiceException {
    CostItineraryAndFactorsEntity entity = testUtil.getCostItineraryAndFactorsEntity();
    entity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(entity));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> bucketValidationService.validateCostItineraryStatus(ORG_ID, COST_FACTOR));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costItineraryAndFactorsRepository, times(1))
        .findCostItinerariesByCostFactor(any(), any());
  }

  @Test
  @DisplayName("Bucket validation is skipped if cost factor is not bucketed")
  void skipValidationIfCostFactorNotBucketed() throws CommonServiceException {
    CostItineraryAndFactorsEntity entity = testUtil.getCostItineraryAndFactorsEntity();
    entity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setIsBucketed(false);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactorEntity));
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(entity));
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateCostFactorBucketRanges(ORG_ID, COST_FACTOR));
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(0))
        .findCostItinerariesByCostFactor(any(), any());
  }

  @Test
  @DisplayName("Discrete buckets - valid")
  void validateDiscreteBucketsPositiveTest() throws CommonServiceException {
    List<CostFactorDiscreteBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Kitchen,Garden,Electronics").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Sports,Grocery,Edibles").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Non-edibles,Paints,Hardware").build());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateDiscreteBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Discrete buckets - invalid")
  void validateDiscreteBucketsNegativeTest() throws CommonServiceException {
    List<CostFactorDiscreteBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Kitchen,Garden,Electronics").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Sports,Kitchen,Edibles").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Non-edibles,Paints,Hardware").build());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateDiscreteBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Discrete buckets case insensitive- invalid ")
  void validateDiscreteBucketsNegativeTestCaseInsensitive() throws CommonServiceException {
    List<CostFactorDiscreteBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Kitchen,Garden,Electronics").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Sports,KiTCHen,Edibles").build());
    entities.add(
        CostFactorDiscreteBucketEntity.builder().valueList("Non-edibles,Paints,Hardware").build());
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateDiscreteBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Contiguous buckets valid even when unsorted")
  void areBucketsValidTest1() throws CommonServiceException {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Contiguous buckets valid even with different boundary condition")
  void areBucketsValidTest1a() throws CommonServiceException {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.0)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(true)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(false)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Non Contiguous buckets invalid")
  void areBucketsValidTest2() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.4)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Overlapping buckets invalid")
  void areBucketsValidTest3() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.6)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Contiguous buckets with 2 minimums invalid")
  void areBucketsValidTest4() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.08)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.08)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Contiguous buckets with 2 maxima invalid")
  void areBucketsValidTest5() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(101.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Start and end both inclusive in successive bucket - invalid")
  void areBucketsValidTest6() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(true)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(150.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Start and end both exclusive in successive bucket - invalid")
  void areBucketsValidTest7() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(false)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(150.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Bucket with only one number - valid")
  void areBucketsValidTest8() {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(150.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(150.0)
            .toValue(150.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(true)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(150.0)
            .toValue(160.0)
            .isFromValueInclusive(false)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(160.0)
            .toValue(null)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertDoesNotThrow(
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }

  @Test
  @DisplayName("Bucket not covering max value - invalid")
  void areBucketsValidTest9() throws CommonServiceException {
    List<CostFactorContiguousBucketEntity> entities = new ArrayList<>();
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(null)
            .toValue(0.05)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.5)
            .toValue(0.7)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.05)
            .toValue(0.5)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(0.7)
            .toValue(100.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    entities.add(
        CostFactorContiguousBucketEntity.builder()
            .orgId(ORG_ID)
            .fromValue(100.0)
            .toValue(150.0)
            .isFromValueInclusive(true)
            .isToValueInclusive(false)
            .build());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(entities);
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> bucketValidationService.validateContiguousBuckets(ORG_ID, COST_FACTOR));
  }
}
