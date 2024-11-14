/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.mapper.CostAttributeMapper;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDetailsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeRequest;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeUpdateRequest;
import com.nextuple.sourcing.cost.config.utils.SourcingCostConfigUtil;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CostAttributeService {

  private final CostAttributeRepository costAttributeRepository;
  private static final CostAttributeMapper INSTANCE = Mappers.getMapper(CostAttributeMapper.class);
  private static final String COST_ATTRIBUTE_DETAILS_NOT_FOUND_EXCEPTION_MESSAGE =
      "Cost attribute details not found";
  private static final String COST_ATTRIBUTE_ID = "costAttributeId";
  private static final String COST_ATTRIBUTE_NAME = "costAttributeName";
  private static final String UNIQUE_COST_ATTRIBUTE_EXCEPTION =
      "Cost attribute already exist for given attribute name";
  private static final String ATTRIBUTE_NAME = "attributeName";

  @Transactional
  public CostAttributeDto createCostAttribute(CostAttributeRequest costAttributeRequest)
      throws CommonServiceException {
    log.debug("-- inside createCostAttribute service --");
    String attributeName = costAttributeRequest.getAttributeName();
    SourcingCostConfigUtil.validateCostAttributeName(attributeName);
    Optional<CostAttributeDetailsEntity> existingCostAttributeEntity =
        costAttributeRepository.findByAttributeName(attributeName);
    if (existingCostAttributeEntity.isPresent()) {
      log.error(UNIQUE_COST_ATTRIBUTE_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ATTRIBUTE_NAME, FieldError.builder().rejectedValue(attributeName).build());
      throw new CommonServiceException(
          UNIQUE_COST_ATTRIBUTE_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
    var costAttributeEntity = INSTANCE.toCostAttributeEntity(costAttributeRequest);
    return INSTANCE.toCostAttributeDto(costAttributeRepository.save(costAttributeEntity));
  }

  @Transactional
  public CostAttributeDto findCostAttributeDetailsById(Long costAttributeId)
      throws CommonServiceException {
    Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
        costAttributeRepository.findById(costAttributeId);
    if (costAttributeDetailsEntity.isEmpty()) {
      log.error(COST_ATTRIBUTE_DETAILS_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_ATTRIBUTE_ID, FieldError.builder().rejectedValue(costAttributeId).build());
      throw new CommonServiceException(
          COST_ATTRIBUTE_DETAILS_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toCostAttributeDto(costAttributeDetailsEntity.get());
  }

  @Transactional
  public CostAttributeDto updateCostAttribute(
      Long costAttributeId, CostAttributeUpdateRequest costAttributeUpdateRequest)
      throws CommonServiceException {
    Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
        costAttributeRepository.findById(costAttributeId);
    if (costAttributeDetailsEntity.isEmpty()) {
      log.error(COST_ATTRIBUTE_DETAILS_NOT_FOUND_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_ATTRIBUTE_ID, FieldError.builder().rejectedValue(costAttributeId).build());
      throw new CommonServiceException(
          COST_ATTRIBUTE_DETAILS_NOT_FOUND_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    INSTANCE.updateCostAttribute(costAttributeUpdateRequest, costAttributeDetailsEntity.get());
    return INSTANCE.toCostAttributeDto(
        costAttributeRepository.save(costAttributeDetailsEntity.get()));
  }

  @Transactional
  public CostAttributeDto findCostAttributeDetailsByAttributeName(String attributeName)
      throws CommonServiceException {
    Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
        costAttributeRepository.findByAttributeName(attributeName);
    if (costAttributeDetailsEntity.isEmpty()) {
      log.error("Cost attribute details not found for given attributeName :{}", attributeName);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(COST_ATTRIBUTE_NAME, FieldError.builder().rejectedValue(attributeName).build());
      throw new CommonServiceException(
          "Cost attribute details not found for given attributeName",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toCostAttributeDto(costAttributeDetailsEntity.get());
  }

  @Transactional
  public List<CostAttributeDto> findCostAttributeDetailsList() {
    List<CostAttributeDetailsEntity> costAttributeDetailsList =
        costAttributeRepository.findByIsPublished(Boolean.TRUE);

    return INSTANCE.toCostAttributeDtoList(costAttributeDetailsList);
  }

  @Transactional
  public List<CostAttributeDto> deleteCostAttributeDetails() {
    List<CostAttributeDetailsEntity> costAttributeDetailsEntityList =
        costAttributeRepository.findAll();
    costAttributeRepository.deleteAll();
    return INSTANCE.toCostAttributeDtoList(costAttributeDetailsEntityList);
  }

  public List<CostAttributeDetailsCacheKeyDto> getAllCostAttributeCacheKeys(Integer limit) {
    return INSTANCE.toCostAttributeCacheKeyResponseList(
        costAttributeRepository.findAllCostAttributeEntities(limit));
  }
}
