/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorBucketTypeEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorContiguousBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorDiscreteBucketEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostFactorEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostItineraryAndFactorsEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorBucketTypeRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorContiguousBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorDiscreteBucketRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostFactorRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostItineraryAndFactorsRepository;
import com.nextuple.sourcing.cost.config.enums.BucketTypeEnum;
import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import com.nextuple.sourcing.cost.config.service.validation.BucketRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketValidationService {
  private final CostFactorBucketTypeRepository costFactorBucketTypeRepository;
  private final CostFactorContiguousBucketRepository costFactorContiguousBucketRepository;
  private final CostFactorDiscreteBucketRepository costFactorDiscreteBucketRepository;
  private final CostItineraryAndFactorsRepository costItineraryAndFactorsRepository;
  private final CostFactorRepository costFactorRepository;

  private static final String COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND =
      "Bucket type not found for orgId and Cost Factor combination.";

  private static final String CONTIGUOUS_BUCKET_RANGES_INVALID =
      "Bucket Ranges defined for cost factor are invalid";

  private static final String DISCRETE_BUCKET_RANGES_INVALID =
      "Discrete Bucket Ranges defined for cost factor are invalid because of repeated values.";
  private static final String COST_FACTOR_ITINERARY_IN_CREATED_STATE =
      "Cannot perform operation if at least one associated cost itinerary is in CREATED state";
  private static final String ORG_ID = "orgId";
  private static final String COST_FACTOR = "costFactor";
  private static final String ID = "id";

  public void validateOrgIdAndCostFactor(String orgId, String costFactor)
      throws CommonServiceException {
    Optional<CostFactorBucketTypeEntity> costFactorBucketTypeOptional =
        costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    if (costFactorBucketTypeOptional.isEmpty()) {
      handleBucketTypeNotFound(orgId, costFactor);
    }
  }

  public void validateCostItineraryStatus(String orgId, String costFactor)
      throws CommonServiceException {
    List<CostItineraryAndFactorsEntity> costItineraryAndFactorsEntities =
        costItineraryAndFactorsRepository.findCostItinerariesByCostFactor(orgId, costFactor);
    costItineraryAndFactorsEntities =
        costItineraryAndFactorsEntities.stream()
            .filter(x -> ItineraryStatusEnum.CREATED.equals(x.getItineraryStatus()))
            .toList();
    if (!costItineraryAndFactorsEntities.isEmpty()) {
      log.error(COST_FACTOR_ITINERARY_IN_CREATED_STATE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          COST_FACTOR_ITINERARY_IN_CREATED_STATE, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
    }
  }

  private void handleBucketTypeNotFound(String orgId, String costFactor)
      throws CommonServiceException {
    log.error(COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND);
    Map<String, FieldError> errorMap = new HashMap<>();
    errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
    errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
    throw new CommonServiceException(
        COST_FACTOR_ORG_ID_COMBINATION_NOT_FOUND, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
  }

  public void validateCostFactorBucketRanges(String orgId, String commaSeparatedCostFactors)
      throws CommonServiceException {
    List<String> costFactorList = Arrays.stream(commaSeparatedCostFactors.split(",")).toList();
    for (String costFactor : costFactorList) {
      CostFactorEntity costFactorEntity =
          costFactorRepository.findByOrgIdAndCostFactor(orgId, costFactor).get(0);
      if (Boolean.TRUE.equals(costFactorEntity.getIsBucketed())) {
        Optional<CostFactorBucketTypeEntity> bucketTypeEntityOptional =
            costFactorBucketTypeRepository.findByOrgIdAndCostFactor(orgId, costFactor);
        if (bucketTypeEntityOptional.isEmpty()) handleBucketTypeNotFound(orgId, costFactor);
        if (BucketTypeEnum.DISCRETE.equals(bucketTypeEntityOptional.get().getBucketType())) {
          validateDiscreteBuckets(orgId, costFactor);
        } else {
          validateContiguousBuckets(orgId, costFactor);
        }
      }
    }
  }

  public void validateDiscreteBuckets(String orgId, String costFactor)
      throws CommonServiceException {
    List<CostFactorDiscreteBucketEntity> discreteBucketEntities =
        costFactorDiscreteBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    List<String> valuesInBucket =
        discreteBucketEntities.stream()
            .flatMap(
                x ->
                    Arrays.stream(x.getValueList().split(","))
                        .map(String::toLowerCase)
                        .toList()
                        .stream())
            .toList();
    Set<String> valueInBucketSet = new HashSet<>(valuesInBucket);
    if (valueInBucketSet.size() < valuesInBucket.size()) {
      log.error(DISCRETE_BUCKET_RANGES_INVALID);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          DISCRETE_BUCKET_RANGES_INVALID, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
    }
  }

  public void validateContiguousBuckets(String orgId, String costFactor)
      throws CommonServiceException {
    List<CostFactorContiguousBucketEntity> contiguousBucketEntities =
        costFactorContiguousBucketRepository.findByOrgIdAndCostFactor(orgId, costFactor);
    List<CostFactorContiguousBucketEntity> bucketsWithFromValueNull =
        contiguousBucketEntities.stream().filter(x -> Objects.isNull(x.getFromValue())).toList();
    List<CostFactorContiguousBucketEntity> bucketsWithToValueNull =
        contiguousBucketEntities.stream().filter(x -> Objects.isNull(x.getToValue())).toList();
    contiguousBucketEntities =
        contiguousBucketEntities.stream()
            .filter(x -> Objects.nonNull(x.getFromValue()))
            .filter(x -> Objects.nonNull(x.getToValue()))
            .collect(Collectors.toList());
    contiguousBucketEntities.sort(
        Comparator.comparing(CostFactorContiguousBucketEntity::getFromValue));
    List<CostFactorContiguousBucketEntity> sortedBuckets = new ArrayList<>();
    sortedBuckets.addAll(bucketsWithFromValueNull);
    sortedBuckets.addAll(contiguousBucketEntities);
    sortedBuckets.addAll(bucketsWithToValueNull);
    boolean isValid = areBucketsValid(sortedBuckets);
    if (!isValid) {
      log.error(CONTIGUOUS_BUCKET_RANGES_INVALID);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(COST_FACTOR, FieldError.builder().rejectedValue(costFactor).build());
      throw new CommonServiceException(
          CONTIGUOUS_BUCKET_RANGES_INVALID, HttpStatus.PRECONDITION_FAILED, 0x1771, errorMap);
    }
  }

  public static boolean areBucketsValid(List<CostFactorContiguousBucketEntity> bucketEntities) {
    double minValue = 0.0;
    double maxValue = Double.MAX_VALUE;
    List<BucketRange> ranges = new ArrayList<>();
    for (CostFactorContiguousBucketEntity bucket : bucketEntities) {
      double fromValue = Objects.isNull(bucket.getFromValue()) ? minValue : bucket.getFromValue();
      double toValue = Objects.isNull(bucket.getToValue()) ? maxValue : bucket.getToValue();
      new BucketRange(
          fromValue,
          toValue,
          bucket.getIsFromValueInclusive(),
          bucket.getIsToValueInclusive(),
          ranges);
    }
    return isValidRange(ranges);
  }

  private static boolean isValidRange(List<BucketRange> ranges) {
    boolean isValidRange = true;
    boolean prevIncl = false;
    Double previousBucketUpperBound = 0.0;
    for (BucketRange range : ranges) {
      if (!isInclusiveRange(
          previousBucketUpperBound,
          range.getFromValue(),
          range.getToValue(),
          range.getIsFromValueInclusive(),
          prevIncl)) {
        isValidRange = false;
        break;
      }
      previousBucketUpperBound = range.getToValue();
      prevIncl = range.getIsToValueInclusive();
    }
    if (isValidRange && previousBucketUpperBound < Double.MAX_VALUE) isValidRange = false;
    return isValidRange;
  }

  private static boolean isInclusiveRange(
      Double previousMax,
      Double rangeStart,
      Double rangeEnd,
      boolean isInclusiveStart,
      boolean isInclPreviousStart) {

    boolean isValidStart =
        isValidStart(isInclPreviousStart, isInclusiveStart, previousMax, rangeStart);
    boolean isValidEnd = isValidEnd(rangeStart, rangeEnd);
    return isValidStart && isValidEnd;
  }

  private static boolean isValidStart(
      boolean currentInclusive, boolean previousInclusive, Double prev, Double start) {
    boolean xor = currentInclusive ^ previousInclusive;
    if (!xor) return false;
    else return prev.equals(start);
  }

  private static boolean isValidEnd(Double start, Double end) {
    return start <= end;
  }
}
