package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorContiguousBucketEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorContiguousBucketDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.inbound.CostFactorContiguousBucketRequest;
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

class CostFactorContiguousBucketServiceImplTest {

  @Mock private CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  @Mock private CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private BucketValidationService bucketValidationService;

  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "Bucket type not found for orgId and Cost Factor combination.";
  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";
  @InjectMocks private TestUtil testUtil;
  @InjectMocks private CostFactorContiguousBucketServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Create cost factor contiguous bucket")
  void createCostFactorContiguousBucket() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CostFactorContiguousBucketDto dto = service.createCostFactorContiguousBucket(ORG_ID, request);
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(), dto.getCustomAttributes());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Create cost factor contiguous bucket - Bucket not found")
  void createCostFactorContiguousBucketNotFound() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorContiguousBucket(ORG_ID, request));

    Assertions.assertEquals(
        "Bucket type not found for orgId and Cost Factor combination.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, commonServiceException.getHttpStatus());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(0))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor contiguous bucket - New record already present")
  void createCostFactorContiguousBucketDuplicateEntry() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorContiguousBucket(ORG_ID, request));

    Assertions.assertEquals(
        "OrgId, Cost Factor and notation combination should be unique.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.CONFLICT, commonServiceException.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor contiguous bucket - Cost itinerary CREATED")
  void createCostFactorContiguousBucketItineraryCREATED() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
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
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorContiguousBucket(ORG_ID, request));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, commonServiceException.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Create cost factor contiguous bucket - Wrong Bucket type")
  void createCostFactorContiguousBucketWrongBucketType() throws CommonServiceException {

    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.createCostFactorContiguousBucket(ORG_ID, request));

    Assertions.assertEquals(
        "Bucket type of cost factor should be CONTIGUOUS", commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, commonServiceException.getHttpStatus());

    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorContiguousBucketRepository, times(0))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Get cost factor contiguous bucket - Happy Path")
  void getCostFactorContiguousBucket() throws CommonServiceException {
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorContiguousBucketEntity()));
    List<CostFactorContiguousBucketDto> dto =
        service.getCostFactorContiguousBuckets(ORG_ID, COST_FACTOR);

    Assertions.assertEquals(ORG_ID, dto.get(0).getOrgId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(),
        dto.getFirst().getCustomAttributes());
    verify(costFactorContiguousBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Get cost factor contiguous bucket - Not found")
  void getCostFactorContiguousBucketNotFound() throws CommonServiceException {
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Collections.emptyList());
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.getCostFactorContiguousBuckets(ORG_ID, COST_FACTOR));

    Assertions.assertEquals(
        "Buckets not found for orgId, Cost Factor combination.",
        commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, commonServiceException.getHttpStatus());
    verify(costFactorContiguousBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Update the cost factor contiguous bucket - Happy path")
  void updateCostFactorContiguousBucket() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CostFactorContiguousBucketDto dto =
        service.updateCostFactorContiguousBucket(ID, ORG_ID, request);

    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(), dto.getCustomAttributes());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Update cost factor contiguous bucket - Bucket not found")
  void updateCostFactorContiguousBucketNotFound() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorContiguousBucket(ID, ORG_ID, request));

    Assertions.assertEquals("Bucket not found for given id and orgId.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(0)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor contiguous bucket - bucket type not found")
  void updateCostFactorContiguousBucketDuplicateEntry() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorContiguousBucket(ID, ORG_ID, request));

    Assertions.assertEquals(
        "Bucket type not found for orgId and Cost Factor combination.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor contiguous bucket - Cost itinerary CREATED")
  void updateCostFactorContiguousBucketItineraryCREATED() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorContiguousBucket(ID, ORG_ID, request));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Update cost factor contiguous bucket - Wrong Bucket type")
  void updateCostFactorContiguousBucketWrongBucketType() throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(testUtil.getCostFactorBucketTypeEntity()));
    CommonServiceException commonServiceException =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.updateCostFactorContiguousBucket(ID, ORG_ID, request));
    Assertions.assertEquals(
        "Bucket type of cost factor should be CONTIGUOUS", commonServiceException.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, commonServiceException.getHttpStatus());
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Delete cost factor contiguous bucket - Happy path")
  void deleteCostFactorContiguousBucket() throws CommonServiceException {
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    when(costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostItineraryAndFactorsEntity()));
    doNothing().when(costFactorContiguousBucketRepository).delete(any());

    CostFactorContiguousBucketDto dto = service.deleteCostFactorContiguousBucket(ID, ORG_ID);
    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(), dto.getCustomAttributes());
    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(1)).delete(any());
  }

  @Test
  @DisplayName("Delete cost factor contiguous bucket - Bucket not found")
  void deleteCostFactorContiguousBucketNotFound() throws CommonServiceException {
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorContiguousBucketRepository).delete(any());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.deleteCostFactorContiguousBucket(ID, ORG_ID));

    Assertions.assertEquals("Bucket not found for given id and orgId.", exception.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(0)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).delete(any());
  }

  @Test
  @DisplayName("Delete cost factor contiguous bucket - Cost Itinerary CREATED")
  void deleteCostFactorContiguousBucketNotFoundItineraryCREATED() throws CommonServiceException {
    when(costFactorContiguousBucketRepository.findByIdAndOrgId(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostFactorContiguousBucketEntity()));
    doThrow(
            new CommonServiceException(
                COST_FACTOR_ITINERARY_IN_CREATED_STATE,
                HttpStatus.PRECONDITION_FAILED,
                0x1771,
                null))
        .when(bucketValidationService)
        .validateCostItineraryStatus(any(), any());
    doNothing().when(costFactorContiguousBucketRepository).delete(any());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> service.deleteCostFactorContiguousBucket(ID, ORG_ID));

    Assertions.assertEquals(
        "Cannot perform operation if at least one associated cost itinerary is in CREATED state",
        exception.getMessage());
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());

    verify(costFactorContiguousBucketRepository, times(1)).findByIdAndOrgId(any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(0)).delete(any());
  }

  @Test
  void getAllCostFactorContiguousBucketCacheKeysTest() {
    List<CostFactorContiguousBucketEntity> costFactorContiguousBucketEntities =
        testUtil.getCostFactorContiguousBucketEntityList();

    when(costFactorContiguousBucketRepository.findAllCostFactorContiguousBucketEntities(any()))
        .thenReturn(costFactorContiguousBucketEntities);

    List<CostFactorContiguousBucketCacheKeyDto> response =
        service.getCostFactorContiguousBucketCacheKeys(2);

    Assertions.assertEquals(3, response.size());
    Assertions.assertEquals(
        costFactorContiguousBucketEntities.get(0).getOrgId(), response.get(0).getOrgId());
  }

  @Test
  @DisplayName(
      "Create cost factor contiguous bucket where IsFromValueInclusive and IsToValueInclusive value is null")
  void createCostFactorContiguousBucketWhereIsFromValueInclusiveIsNull()
      throws CommonServiceException {
    CostFactorContiguousBucketRequest request = testUtil.getCostFactorContiguousBucketRequest();
    request.setIsFromValueInclusive(null);
    request.setIsToValueInclusive(null);
    doNothing().when(bucketValidationService).validateOrgIdAndCostFactor(any(), any());
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactorAndNotation(
            any(), any(), any()))
        .thenReturn(Optional.empty());
    doNothing().when(bucketValidationService).validateCostItineraryStatus(any(), any());
    when(costFactorContiguousBucketRepository.save(any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntity());
    CostFactorBucketTypeEntity costFactorBucketTypeEntity =
        testUtil.getCostFactorBucketTypeEntity();
    costFactorBucketTypeEntity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(anyString(), anyString()))
        .thenReturn(Optional.of(costFactorBucketTypeEntity));
    CostFactorContiguousBucketDto dto = service.createCostFactorContiguousBucket(ORG_ID, request);
    verify(costFactorBucketTypeRepository, times(1))
        .findByOrgIdAndCostFactor(anyString(), anyString());
    Assertions.assertEquals(ORG_ID, dto.getOrgId());
    assertEquals(
        testUtil.getCostAttributeMappingRequest().getCustomAttributes(), dto.getCustomAttributes());
    Assertions.assertTrue(dto.getIsFromValueInclusive());
    Assertions.assertFalse(dto.getIsToValueInclusive());
    verify(bucketValidationService, times(1)).validateOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1))
        .findByOrgIdAndCostFactorAndNotation(any(), any(), any());
    verify(bucketValidationService, times(1)).validateCostItineraryStatus(any(), any());
    verify(costFactorContiguousBucketRepository, times(1)).save(any());
  }
}
