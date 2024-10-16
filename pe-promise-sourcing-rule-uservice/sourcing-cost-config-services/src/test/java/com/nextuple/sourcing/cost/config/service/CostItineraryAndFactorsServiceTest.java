package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.COST_ITINERARY_IN_DRAFT_STATE;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.COST_ITINERARY_IS_DEFAULT_ITINERARY;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.COST_ITINERARY_IS_ONLY_ITINERARY;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.DELETE_COST_ITINERARY;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE;
import static com.nextuple.sourcing.cost.config.service.CostItineraryAndFactorsService.UPDATE_COST_ITINERARY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_FACTOR_ITINERARY_ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.COST_ITINERARY;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ID;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.INVALID_LEVEL_APPLIED_EXCEPTION;
import static com.nextuple.sourcing.cost.config.utils.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostValueRepository;
import com.nextuple.sourcing.cost.config.domain.repository.SelectorAndCostItineraryMappingRepository;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostItineraryAndFactorsDto;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.enums.LevelAppliedEnum;
import com.nextuple.sourcing.cost.config.inbound.CostItineraryAndFactorsRequest;
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

class CostItineraryAndFactorsServiceTest {
  @Mock private CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  @Mock private CostFactorRepository costFactorRepository;
  @Mock private CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  @Mock private CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  @Mock private CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  @Mock private BucketValidationService bucketValidationService;
  @Mock private CostValueRepository costValueRepository;
  @Mock private SelectorAndCostItineraryMappingRepository selectorAndCostItineraryMappingRepository;
  @InjectMocks private CostItineraryAndFactorsService costItineraryAndFactorsService;
  @InjectMocks private TestUtil testUtil;

  private static final String COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost Factor not found for given orgId.";
  private static final String COST_ITINERARY_ALREADY_PRESENT_EXCEPTION_MESSAGE =
      "Duplicate cost itinerary not allowed for given orgId.";

  private static final String COST_ITINERARY_ALREADY_CREATED_EXCEPTION_MESSAGE =
      "Cost itinerary is in CREATED state and cannot be updated.";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCostItineraryAndFactorsTest() throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactorIn(anyString(), anyList()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.createCostItineraryAndFactors(
            ORG_ID, costItineraryAndFactorsRequest);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getId(),
        costItineraryAndFactorsResponse.getId());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactorIn(anyString(), anyList());
  }

  @Test
  @DisplayName(
      "Creating cost itinerary with level applied as ORDER and cost factor level applied also ORDER")
  void createCostItineraryAndFactorsTestWithOrderLevel() throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setLevelApplied(LevelAppliedEnum.ORDER);
    CostFactorEntity costFactorEntity = testUtil.getCostFactorEntity();
    costFactorEntity.setLevelApplied(LevelAppliedEnum.ORDER);
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactorEntity));

    CostItineraryAndFactorsDto costItineraryAndFactorsResponse =
        costItineraryAndFactorsService.createCostItineraryAndFactors(
            ORG_ID, costItineraryAndFactorsRequest);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getId(),
        costItineraryAndFactorsResponse.getId());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Creating cost itinerary with duplicate itinerary values fails")
  void createCostItineraryAndFactorsDuplicateCostItineraryTest() throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(COST_ITINERARY_ALREADY_PRESENT_EXCEPTION_MESSAGE, e.getMessage());
    Assertions.assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(1)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Creating cost itinerary with missing cost factor fails")
  void createCostItineraryAndFactorsCostFactorMissingTest() throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostItineraryAndFactorsEntity()));
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Collections.emptyList());

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE, e.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Creating cost itinerary with invalid level applied of cost factor fails")
  void createCostItineraryAndFactorsInvalidCostFactorLevelAppliedTestCase1()
      throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setLevelApplied(LevelAppliedEnum.ITEM);
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(INVALID_LEVEL_APPLIED_EXCEPTION, e.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Creating cost itinerary with invalid level applied of cost factor fails")
  void createCostItineraryAndFactorsInvalidCostFactorLevelAppliedTestCase2()
      throws CommonServiceException {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setLevelApplied(LevelAppliedEnum.ITEM_QTY);
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());

    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setLevelApplied(LevelAppliedEnum.ITEM);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(INVALID_LEVEL_APPLIED_EXCEPTION, e.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Creating cost itinerary with invalid level applied of cost factor fails")
  void createCostItineraryAndFactorsInvalidCostFactorLevelAppliedTestCase3() {
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setLevelApplied(LevelAppliedEnum.ORDER);
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());

    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setLevelApplied(LevelAppliedEnum.ITEM);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(INVALID_LEVEL_APPLIED_EXCEPTION, e.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName(
      "Error: Creating cost itinerary with one of the cost factors set as isRateCardLookup to false")
  void createCostItineraryAndFactors_isRateCardLookupTest() throws CommonServiceException {
    var costFactor1 = testUtil.getCostFactorEntity();
    costFactor1.setCostFactor("zone");
    costFactor1.setIsRateCardLookUpRequired(false);
    var costFactor2 = testUtil.getCostFactorEntity();
    List<CostFactorEntity> costFactorEntityList = List.of(costFactor1, costFactor2);
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setCostFactors(
        String.join(",", costFactor1.getCostFactor(), costFactor2.getCostFactor()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(costFactorEntityList);
    when(costFactorRepository.findByOrgIdAndCostFactorIn(anyString(), anyList()))
        .thenReturn(costFactorEntityList);

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(
        ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE, e.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());

    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactorIn(anyString(), anyList());
  }

  @Test
  void findCostItineraryAndFactorsByOrgIdAndIdTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));

    CostItineraryAndFactorsDto preferenceSelectorResponse =
        costItineraryAndFactorsService.findCostItineraryAndFactorsByOrgIdAndId(
            ORG_ID, COST_FACTOR_ITINERARY_ID);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getId(), preferenceSelectorResponse.getId());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  void findCostItineraryAndFactorsByOrgIdAndIdWhenEntityIsNotThereInDBTest() {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.findCostItineraryAndFactorsByOrgIdAndId(
                  ORG_ID, COST_FACTOR_ITINERARY_ID);
            });

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
  }

  @Test
  @DisplayName(
      "Should find cost itinerary and factors by orgId, cost itinerary, itinerary status and isActive")
  void findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveTest()
      throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
            anyString(), anyString(), any(), anyBoolean()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));

    CostItineraryAndFactorsDto costItineraryAndFactorsDto =
        costItineraryAndFactorsService
            .findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                ORG_ID, COST_ITINERARY, ItineraryStatusEnum.CREATED, Boolean.TRUE);

    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getId(), costItineraryAndFactorsDto.getId());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostItinerary(),
        costItineraryAndFactorsDto.getCostItinerary());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostFactors(),
        costItineraryAndFactorsDto.getCostFactors());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getItineraryStatus(),
        costItineraryAndFactorsDto.getItineraryStatus());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getIsActive(),
        costItineraryAndFactorsDto.getIsActive());

    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
            anyString(), anyString(), any(), anyBoolean());
  }

  @Test
  @DisplayName(
      "Should throw exception when cost itinerary and factors not found by orgId, cost itinerary, itinerary status and isActive")
  void
      findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActiveWhenEntityIsNotThereInDBTest() {
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
            anyString(), anyString(), any(), anyBoolean()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService
                  .findCostItineraryAndFactorsByOrgIdCostItineraryItineraryStatusAndIsActive(
                      ORG_ID, COST_ITINERARY, ItineraryStatusEnum.CREATED, Boolean.TRUE);
            });

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItineraryAndItineraryStatusAndIsActive(
            anyString(), anyString(), any(), anyBoolean());
  }

  @Test
  void updateCostItineraryAndFactorsTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactorIn(anyString(), anyList()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));

    CostItineraryAndFactorsDto costFactorResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactors(
            COST_FACTOR_ITINERARY_ID, ORG_ID, testUtil.getUpsertCostItineraryAndFactorsRequest());
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostFactors(),
        costFactorResponse.getCostFactors());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactorIn(anyString(), anyList());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating CREATED status cost itinerary fails")
  void updateCostItineraryAndFactorsInCreatedStatusTest() throws CommonServiceException {
    CostItineraryAndFactorsEntity entity = testUtil.getCostItineraryAndFactorsEntity();
    entity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(entity));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactors(
                    COST_FACTOR_ITINERARY_ID,
                    ORG_ID,
                    testUtil.getUpsertCostItineraryAndFactorsRequest()));

    assertEquals(COST_ITINERARY_ALREADY_CREATED_EXCEPTION_MESSAGE, ex.getMessage());
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary with missing cost factor fails")
  void updateCostItineraryAndFactorsCostFactorMissingTest() throws CommonServiceException {
    CostItineraryAndFactorsEntity entity = testUtil.getCostItineraryAndFactorsEntity();
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(entity));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.updateCostItineraryAndFactors(
                  COST_FACTOR_ITINERARY_ID,
                  ORG_ID,
                  testUtil.getUpsertCostItineraryAndFactorsRequest());
            });
    Assertions.assertEquals(COST_FACTOR_NOT_FOUND_EXCEPTION_MESSAGE, e.getMessage());
    Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costItineraryAndFactorsRepository, times(0)).findByOrgIdAndCostItinerary(any(), any());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Updating cost itinerary which is associated in cost value table fails")
  void updateCostItineraryAndFactorsInCostTableTest() {
    CostItineraryAndFactorsEntity entity = testUtil.getCostItineraryAndFactorsEntity();
    entity.setItineraryStatus(ItineraryStatusEnum.CREATED);
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(entity));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostValueEntity(Boolean.FALSE)));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactors(
                    COST_FACTOR_ITINERARY_ID,
                    ORG_ID,
                    testUtil.getUpsertCostItineraryAndFactorsRequest()));

    assertEquals(
        COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE + UPDATE_COST_ITINERARY, ex.getMessage());
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costValueRepository, times(1))
        .findFirstByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void updateCostItineraryAndFactorsWhenEntityIsNotThereInDBTest() {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactors(
                    COST_FACTOR_ITINERARY_ID,
                    ORG_ID,
                    testUtil.getUpsertCostItineraryAndFactorsRequest()));

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void updateCostItineraryAndFactorsStatusTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    doNothing().when(bucketValidationService).validateCostFactorBucketRanges(any(), anyString());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,L");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.DISCRETE);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntityList());

    CostItineraryAndFactorsDto costFactorResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
            COST_FACTOR_ITINERARY_ID, ORG_ID);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getItineraryStatus(),
        costFactorResponse.getItineraryStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Bucket validation skipped for non bucketed cost factor")
  void updateCostItineraryAndFactorsStatusNonBucketedCostFactor() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setIsBucketed(Boolean.FALSE);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());
    CostItineraryAndFactorsDto response =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
            COST_FACTOR_ITINERARY_ID, ORG_ID);

    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Bucket type not found")
  void updateCostItineraryAndFactorsStatusBucketNotFoundTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,L");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorDiscreteBucketRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Validation fails cost factor values when default value not found")
  void updateCostItineraryAndFactorsStatusDefaultValueNotFoundTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("");
    costFactor.setDefaultValue(null);
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Validation fails cost factor values when values not found")
  void updateCostItineraryAndFactorsStatusValuesNotFoundTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues(null);
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Validation fails cost factor values when values field is empty")
  void updateCostItineraryAndFactorsStatusValuesEmptyTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("");
    costFactor.setDefaultValue("");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(0)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Validation fails cost factor values for discrete buckets")
  void updateCostItineraryAndFactorsStatusBucketValidationTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,X");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.DISCRETE);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntityList());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Validation fails cost factor default value for discrete buckets")
  void updateCostItineraryAndFactorsStatusBucketValidationDefaultValueTest()
      throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,L");
    costFactor.setDefaultValue("X");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.DISCRETE);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntityList());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Status change Happy path with continuous buckets")
  void updateCostItineraryAndFactorsStatusContiguousBucketTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,L");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntityList());

    CostItineraryAndFactorsDto costFactorResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
            COST_FACTOR_ITINERARY_ID, ORG_ID);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getItineraryStatus(),
        costFactorResponse.getItineraryStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorContiguousBucketRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
  }

  @Test
  @DisplayName("Validation fails cost factor values for contiguous buckets")
  void updateCostItineraryAndFactorsStatusContiguousBucketValidationTest()
      throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,X,M");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.CONTIGUOUS);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorContiguousBucketEntityList());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));
    Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED, exception.getHttpStatus());
    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costFactorBucketTypeRepository, times(1)).findByOrgIdAndCostFactor(any(), any());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void updateCostItineraryAndFactorsStatusWhenEntityIsNotThereInDBTest() {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(
                    COST_FACTOR_ITINERARY_ID, ORG_ID));

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary by id - Cost itinerary is in CREATED itinerary status")
  void updateCostItineraryAndFactorsStatusByIdCreatedTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.of(costItinerary));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () -> costItineraryAndFactorsService.updateCostItineraryAndFactorsStatus(ID, ORG_ID));

    assertEquals("Cost itinerary is in CREATED state and cannot be updated.", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary by cost itinerary")
  void updateCostItineraryAndFactorsStatusByCostItineraryTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    doNothing().when(bucketValidationService).validateCostFactorBucketRanges(any(), anyString());
    CostFactorEntity costFactor = testUtil.getCostFactorEntity();
    costFactor.setValues("S,M,L");
    costFactor.setDefaultValue("S");
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(costFactor));
    CostFactorBucketTypeEntity entity = testUtil.getCostFactorBucketTypeEntity();
    entity.setBucketType(BucketTypeEnum.DISCRETE);
    when(costFactorBucketTypeRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(Optional.of(entity));
    when(costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(testUtil.getCostFactorDiscreteBucketEntityList());

    CostItineraryAndFactorsDto costFactorResponse =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsStatusByCostItinerary(
            COST_ITINERARY, ORG_ID);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostFactors(),
        costFactorResponse.getCostFactors());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary by cost itinerary - Cost itinerary not found")
  void updateCostItineraryAndFactorsStatusByCostItineraryNotFoundTest() {
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatusByCostItinerary(
                    COST_ITINERARY, ORG_ID));

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName(
      "Updating cost itinerary by cost itinerary - Cost itinerary is in CREATED itinerary status")
  void updateCostItineraryAndFactorsStatusByCostItineraryCreatedTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);
    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItinerary));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.updateCostItineraryAndFactorsStatusByCostItinerary(
                    COST_ITINERARY, ORG_ID));

    assertEquals("Cost itinerary is in CREATED state and cannot be updated.", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary active status by cost itinerary - Happy Path")
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryTest()
      throws CommonServiceException {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItinerary));
    when(selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString()))
        .thenReturn(List.of());
    when(selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString()))
        .thenReturn(List.of());
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(costItinerary);

    var response =
        costItineraryAndFactorsService.updateCostItineraryAndFactorsActiveStatusByCostItinerary(
            COST_ITINERARY, ORG_ID, true);

    assertEquals(costItinerary.getCostItinerary(), response.getCostItinerary());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary active status by cost itinerary - Itinerary not found")
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryNotFoundTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService
                    .updateCostItineraryAndFactorsActiveStatusByCostItinerary(
                        COST_ITINERARY, ORG_ID, true));

    assertEquals(COST_ITINERARY_AND_FACTORS_NOT_FOUND_EXCEPTION_MESSAGE, ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary active status by cost itinerary - Default Itinerary")
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryDefaultItineraryTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItinerary));
    when(selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService
                    .updateCostItineraryAndFactorsActiveStatusByCostItinerary(
                        COST_ITINERARY, ORG_ID, false));

    assertEquals(COST_ITINERARY_IS_DEFAULT_ITINERARY, ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary active status by cost itinerary - Itinerary in DRAFT state")
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryDraftStateTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.DRAFT);

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItinerary));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService
                    .updateCostItineraryAndFactorsActiveStatusByCostItinerary(
                        COST_ITINERARY, ORG_ID, true));

    assertEquals(COST_ITINERARY_IN_DRAFT_STATE, ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(selectorAndCostItineraryMappingRepository, times(0))
        .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Updating cost itinerary active status by cost itinerary - Only Itinerary")
  void updateCostItineraryAndFactorsActiveStatusByCostItineraryOnlyItineraryTest() {
    CostItineraryAndFactorsEntity costItinerary = testUtil.getCostItineraryAndFactorsEntity();
    costItinerary.setItineraryStatus(ItineraryStatusEnum.CREATED);

    when(costItineraryAndFactorsRepository.findByOrgIdAndCostItinerary(anyString(), anyString()))
        .thenReturn(Optional.of(costItinerary));
    when(selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString()))
        .thenReturn(List.of());
    when(selectorAndCostItineraryMappingRepository
            .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString()))
        .thenReturn(List.of(testUtil.getSelectorAndCostItineraryMappingEntity()));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService
                    .updateCostItineraryAndFactorsActiveStatusByCostItinerary(
                        COST_ITINERARY, ORG_ID, false));

    assertEquals(COST_ITINERARY_IS_ONLY_ITINERARY, ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1))
        .findByOrgIdAndCostItinerary(anyString(), anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findBySelectorCfIsNotNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(selectorAndCostItineraryMappingRepository, times(1))
        .findBySelectorCfIsNullAndSelectorCfValueIsNullAndCostItinerary(anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName(
      "Error: Updating cost itinerary with one of the cost factors set as isRateCardLookup to false")
  void updateCostItineraryAndFactors_isRateCardLookupTest() throws CommonServiceException {
    var costFactor1 = testUtil.getCostFactorEntity();
    costFactor1.setCostFactor("zone");
    costFactor1.setIsRateCardLookUpRequired(false);
    var costFactor2 = testUtil.getCostFactorEntity();
    List<CostFactorEntity> costFactorEntityList = List.of(costFactor1, costFactor2);
    CostItineraryAndFactorsRequest costItineraryAndFactorsRequest =
        testUtil.getUpsertCostItineraryAndFactorsRequest();
    costItineraryAndFactorsRequest.setCostFactors(
        String.join(",", costFactor1.getCostFactor(), costFactor2.getCostFactor()));
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costItineraryAndFactorsRepository.save(any(CostItineraryAndFactorsEntity.class)))
        .thenReturn(testUtil.getCostItineraryAndFactorsEntity());
    when(costFactorRepository.findByOrgIdAndCostFactor(any(), any()))
        .thenReturn(List.of(testUtil.getCostFactorEntity()));
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.empty());
    when(costFactorRepository.findByOrgIdAndCostFactorIn(anyString(), anyList()))
        .thenReturn(costFactorEntityList);

    CommonServiceException e =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              costItineraryAndFactorsService.createCostItineraryAndFactors(
                  ORG_ID, costItineraryAndFactorsRequest);
            });
    Assertions.assertEquals(
        ITINERARY_COST_FACTOR_RATE_CARD_LOOKUP_EXCEPTION_MESSAGE, e.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    Assertions.assertEquals(ORG_ID, e.getFieldInfo().get("orgId").getRejectedValue());

    verify(costFactorRepository, times(1)).findByOrgIdAndCostFactorIn(anyString(), anyList());
    verify(costItineraryAndFactorsRepository, times(0))
        .save(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void deleteCostItineraryAndFactorsTest() throws CommonServiceException {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    doNothing()
        .when(costItineraryAndFactorsRepository)
        .delete(any(CostItineraryAndFactorsEntity.class));

    CostItineraryAndFactorsDto costFactorResponse =
        costItineraryAndFactorsService.deleteCostItineraryAndFactors(
            ORG_ID, COST_FACTOR_ITINERARY_ID);
    assertEquals(
        testUtil.getCostItineraryAndFactorsEntity().getCostFactors(),
        costFactorResponse.getCostFactors());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(1))
        .delete(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void deleteCostItineraryAndFactorsWhenEntityIsNotThereInDBTest() {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.empty());

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.deleteCostItineraryAndFactors(
                    ORG_ID, COST_FACTOR_ITINERARY_ID));

    assertEquals("Cost Itinerary And Cost Factors Mapping not found", ex.getMessage());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .delete(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  @DisplayName("Deleting cost itinerary which is associated in cost value table fails")
  void deleteCostItineraryAndFactorsInCostTableTest() {
    when(costItineraryAndFactorsRepository.findByIdAndOrgId(anyLong(), anyString()))
        .thenReturn(Optional.ofNullable(testUtil.getCostItineraryAndFactorsEntity()));
    when(costValueRepository.findFirstByOrgIdAndCostItinerary(any(), any()))
        .thenReturn(Optional.of(testUtil.getCostValueEntity(Boolean.FALSE)));

    CommonServiceException ex =
        assertThrows(
            CommonServiceException.class,
            () ->
                costItineraryAndFactorsService.deleteCostItineraryAndFactors(
                    ORG_ID, COST_FACTOR_ITINERARY_ID));

    assertEquals(
        COST_ITINERARY_ASSOCIATED_IN_COST_VALUE_TABLE + DELETE_COST_ITINERARY, ex.getMessage());
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
    verify(costItineraryAndFactorsRepository, times(1)).findByIdAndOrgId(anyLong(), anyString());
    verify(costValueRepository, times(1))
        .findFirstByOrgIdAndCostItinerary(anyString(), anyString());
    verify(costItineraryAndFactorsRepository, times(0))
        .delete(any(CostItineraryAndFactorsEntity.class));
  }

  @Test
  void getAllCostItineraryAndFactorsCacheKeysTest() {
    List<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntities =
        testUtil.getCostItineraryAndFactorsEntityList();

    when(costItineraryAndFactorsRepository.findAllCostItineraryAndFactorsEntities(any()))
        .thenReturn(costItineraryAndFactorsEntities);

    List<CostItineraryAndFactorsCacheKeyDto> response =
        costItineraryAndFactorsService.getCostItineraryAndFactorsCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        costItineraryAndFactorsEntities.get(0).getOrgId(), response.get(0).getOrgId());
    verify(costItineraryAndFactorsRepository, VerificationModeFactory.times(1))
        .findAllCostItineraryAndFactorsEntities(any());
  }
}
