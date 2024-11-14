/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.service;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionScopeEnum;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingAttributesDefinitionStatus;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingAttributesDefinitionUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributeDefinitionUIResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingAttributesDefinitionResponse;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.AttributeInfo;
import com.nextuple.promise.sourcing.rule.domain.mapper.SourcingAttributesDefinitionMapper;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributeDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.domain.SourcingAttributesDefinitionDomainDto;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributePersistenceService;
import com.nextuple.promise.sourcing.rule.persistence.service.SourcingAttributesDefinitionPersistenceService;
import com.nextuple.promise.sourcing.rule.utils.PromiseSourcingRuleUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SourcingAttributesDefinitionService {

  private static final Logger logger =
      LoggerFactory.getLogger(SourcingAttributesDefinitionService.class);
  private static final String ID = "id";

  private static final String ORG_ID = "orgId";
  private static final String ATTRIBUTE_ID = "attributeId";
  private static final String REQUIRED_ATTRIBUTES = "reqAttributes";
  private static final String ATTRIBUTES_DEFINITION_NAME = "attributesDefinitionName";
  private static final String SOURCING_RULE_ATTRIBUTES_DEFINITION_EXCEPTION_MESSAGE =
      "Sourcing attributes definition not found";

  private static final String SCOPE = "sourcingAttributesDefinitionScope";

  private static final SourcingAttributesDefinitionMapper INSTANCE =
      Mappers.getMapper(SourcingAttributesDefinitionMapper.class);

  private final SourcingAttributesDefinitionPersistenceService
      sourcingAttributesDefinitionPersistenceService;
  private final SourcingAttributePersistenceService sourcingAttributePersistenceService;

  public SourcingAttributesDefinitionResponse processCreateSourcingAttributesDefinition(
      SourcingAttributesDefinitionRequest sourcingRuleAttributesDefinitionRequest)
      throws CommonServiceException, PromiseEngineException {

    validateName(
        sourcingRuleAttributesDefinitionRequest.getName(),
        sourcingRuleAttributesDefinitionRequest.getOrgId());

    if (sourcingRuleAttributesDefinitionRequest
        .getStatus()
        .equals(SourcingAttributesDefinitionStatus.ACTIVE)) {
      checkForAttributeDefinitionInActiveStatus(
          sourcingRuleAttributesDefinitionRequest.getOrgId(),
          sourcingRuleAttributesDefinitionRequest.getScope());
    }

    validateSourcingAttributeReferences(
        sourcingRuleAttributesDefinitionRequest.getReqAttributes(),
        sourcingRuleAttributesDefinitionRequest.getOptAttributes(),
        sourcingRuleAttributesDefinitionRequest.getOrgId());

    var sourcingAttributesDefinitionDomainDto =
        INSTANCE.toSourcingRuleAttributesDefinitionEntity(sourcingRuleAttributesDefinitionRequest);
    return INSTANCE.toSourcingRuleAttributesDefinitionResponse(
        sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            sourcingAttributesDefinitionDomainDto));
  }

  private void validateSourcingAttributeReferences(
      String reqAttributes, String optAttributes, String orgId)
      throws CommonServiceException, PromiseEngineException {

    String attributeReferences = StringUtils.hasLength(reqAttributes) ? reqAttributes : "";
    if (StringUtils.hasLength(optAttributes)) {
      attributeReferences =
          StringUtils.hasLength(reqAttributes)
              ? attributeReferences + "," + optAttributes
              : optAttributes;
    }

    List<SourcingAttributeDomainDto> sourcingAttributeDomainDtoList =
        sourcingAttributePersistenceService.getSourcingAttributeListByOrgId(orgId);
    List<Long> attributeIds =
        sourcingAttributeDomainDtoList.stream().map(SourcingAttributeDomainDto::getId).toList();

    HashSet<String> attributeSet = new HashSet<>();
    String[] attributeReferencesList = attributeReferences.split("\\s*,\\s*");
    for (String attr : attributeReferencesList) {
      if (!attr.matches("[0-9]+")) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ATTRIBUTE_ID, FieldError.builder().rejectedValue(attr).build());
        throw new CommonServiceException(
            "Attribute can have only numeric values", HttpStatus.BAD_REQUEST, 0X1771, errorMap);
      }
      if (attributeSet.contains(attr)) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(REQUIRED_ATTRIBUTES, FieldError.builder().rejectedValue(attr).build());
        throw new CommonServiceException(
            "Required Attributes and Optional Attributes can't be the same",
            HttpStatus.BAD_REQUEST,
            0X1771,
            errorMap);
      }
      attributeSet.add(attr);
      if (!attributeIds.contains(Long.parseLong(attr))) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(
            ATTRIBUTE_ID, FieldError.builder().rejectedValue(Long.parseLong(attr)).build());
        throw new CommonServiceException(
            "Attribute not found with given attribute id", HttpStatus.NOT_FOUND, 0X1771, errorMap);
      }
    }
  }

  private void checkForAttributeDefinitionInActiveStatus(
      String orgId, SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {

    List<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDtoList =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                orgId, SourcingAttributesDefinitionStatus.ACTIVE, scope);

    if (!sourcingAttributesDefinitionDomainDtoList.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SCOPE, FieldError.builder().rejectedValue(scope).build());
      throw new CommonServiceException(
          "A sourcing attributes definition is already active for given orgId and scope",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  private void validateName(String name, String orgId)
      throws CommonServiceException, PromiseEngineException {
    PromiseSourcingRuleUtil.validateNameFormat(name);
    List<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDtoList =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndName(orgId, name);
    if (!sourcingAttributesDefinitionDomainDtoList.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ATTRIBUTES_DEFINITION_NAME, FieldError.builder().rejectedValue(name).build());
      throw new CommonServiceException(
          "Combination of orgId and name should be unique",
          HttpStatus.BAD_REQUEST,
          0X1771,
          errorMap);
    }
  }

  public SourcingAttributesDefinitionResponse processGetSourcingAttributesDefinitionByIdandOrgId(
      Long id, String orgId) throws PromiseEngineException, CommonServiceException {

    Optional<SourcingAttributesDefinitionDomainDto> sourcingRuleAttributesDefinitionDomainDto =
        sourcingAttributesDefinitionPersistenceService
            .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(id, orgId);
    if (sourcingRuleAttributesDefinitionDomainDto.isEmpty()) {
      logger.error(SOURCING_RULE_ATTRIBUTES_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          SOURCING_RULE_ATTRIBUTES_DEFINITION_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
    return INSTANCE.toSourcingRuleAttributesDefinitionResponse(
        sourcingRuleAttributesDefinitionDomainDto.get());
  }

  public SourcingAttributesDefinitionResponse updateSourcingAttributesDefinition(
      Long id,
      String orgId,
      SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributesDefinitionDomainDto>
        existingSourcingRuleAttributesDefinitionDomainDto =
            sourcingAttributesDefinitionPersistenceService
                .getSourcingRuleAttributesDefinitionEntityByIdAndOrgId(id, orgId);
    if (existingSourcingRuleAttributesDefinitionDomainDto.isEmpty()) {
      logger.error(SOURCING_RULE_ATTRIBUTES_DEFINITION_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
      throw new CommonServiceException(
          SOURCING_RULE_ATTRIBUTES_DEFINITION_EXCEPTION_MESSAGE,
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }

    SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto =
        existingSourcingRuleAttributesDefinitionDomainDto.get();
    validateForActiveAndInactiveStatus(
        sourcingAttributesDefinitionDomainDto, sourcingRuleAttributesDefinitionUpdationRequest);
    if (sourcingRuleAttributesDefinitionUpdationRequest.getName() != null
        && !sourcingRuleAttributesDefinitionUpdationRequest
            .getName()
            .equals(sourcingAttributesDefinitionDomainDto.getName())
        && StringUtils.hasLength(sourcingRuleAttributesDefinitionUpdationRequest.getName()))
      validateName(
          sourcingRuleAttributesDefinitionUpdationRequest.getName(),
          sourcingAttributesDefinitionDomainDto.getOrgId());

    if (Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getStatus())
        && sourcingRuleAttributesDefinitionUpdationRequest
            .getStatus()
            .equals(SourcingAttributesDefinitionStatus.ACTIVE)) {
      checkForAttributeDefinitionInActiveStatus(
          sourcingAttributesDefinitionDomainDto.getOrgId(),
          sourcingAttributesDefinitionDomainDto.getScope());
    }
    if (StringUtils.hasLength(sourcingRuleAttributesDefinitionUpdationRequest.getReqAttributes())
        || StringUtils.hasLength(
            sourcingRuleAttributesDefinitionUpdationRequest.getOptAttributes()))
      validateSourcingAttributeReferences(
          sourcingRuleAttributesDefinitionUpdationRequest.getReqAttributes(),
          sourcingRuleAttributesDefinitionUpdationRequest.getOptAttributes(),
          sourcingAttributesDefinitionDomainDto.getOrgId());

    if (sourcingAttributesDefinitionDomainDto
        .getStatus()
        .equals(SourcingAttributesDefinitionStatus.DRAFT)) {
      checkForBlankValuesOfUpdateRequest(sourcingRuleAttributesDefinitionUpdationRequest);
      INSTANCE.updateSourcingAttributesDefinitionEntity(
          sourcingRuleAttributesDefinitionUpdationRequest, sourcingAttributesDefinitionDomainDto);
    }

    if (Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getStatus())) {
      if (sourcingRuleAttributesDefinitionUpdationRequest
              .getStatus()
              .equals(SourcingAttributesDefinitionStatus.DRAFT)
          && !existingSourcingRuleAttributesDefinitionDomainDto
              .get()
              .getStatus()
              .equals(SourcingAttributesDefinitionStatus.DRAFT)) {
        logger.error(
            "The given attributes definition can only be activated/deactivated , can't be changed to draft status");
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ID, FieldError.builder().rejectedValue(id).build());
        throw new CommonServiceException(
            "The given attributes definition can only be activated/deactivated , can't be changed to draft status",
            HttpStatus.NOT_FOUND,
            0x1771,
            errorMap);
      }
      sourcingAttributesDefinitionDomainDto.setStatus(
          sourcingRuleAttributesDefinitionUpdationRequest.getStatus());
    }
    return INSTANCE.toSourcingRuleAttributesDefinitionResponse(
        sourcingAttributesDefinitionPersistenceService.saveSourcingRuleAttributesDefinitionEntity(
            sourcingAttributesDefinitionDomainDto));
  }

  private void validateForActiveAndInactiveStatus(
      SourcingAttributesDefinitionDomainDto sourcingAttributesDefinitionDomainDto,
      SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest)
      throws CommonServiceException {
    if (!sourcingAttributesDefinitionDomainDto
            .getStatus()
            .equals(SourcingAttributesDefinitionStatus.DRAFT)
        && (Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getName())
            || Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getOptAttributes())
            || Objects.nonNull(
                sourcingRuleAttributesDefinitionUpdationRequest.getReqAttributes()))) {
      throw new CommonServiceException(
          "Can't update the attribute definition details as it's in ACTIVE/INACTIVE statue.Only the status field can be modified!",
          HttpStatus.BAD_REQUEST,
          0X1771,
          null);
    }
  }

  private void checkForBlankValuesOfUpdateRequest(
      SourcingAttributesDefinitionUpdationRequest sourcingRuleAttributesDefinitionUpdationRequest)
      throws CommonServiceException {

    if ((Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getReqAttributes())
            && sourcingRuleAttributesDefinitionUpdationRequest.getReqAttributes().isEmpty())
        || (Objects.nonNull(sourcingRuleAttributesDefinitionUpdationRequest.getName())
            && sourcingRuleAttributesDefinitionUpdationRequest.getName().isEmpty())) {
      throw new CommonServiceException(
          "Can't assign a  blank value to name / reqAttributes field",
          HttpStatus.BAD_REQUEST,
          0X1771,
          null);
    }
  }

  public SourcingAttributesDefinitionResponse processGetSourcingAttributesDefinitionInActiveStatus(
      String orgId, SourcingAttributesDefinitionScopeEnum scope)
      throws PromiseEngineException, CommonServiceException {

    List<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDtoList =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                orgId, SourcingAttributesDefinitionStatus.ACTIVE, scope);

    if (sourcingAttributesDefinitionDomainDtoList.isEmpty()) {
      if (scope.equals(SourcingAttributesDefinitionScopeEnum.SOURCING_RULE)) {
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
        errorMap.put(SCOPE, FieldError.builder().rejectedValue(scope).build());
        throw new CommonServiceException(
            "No active sourcing rule attributes definition exists for given orgId and scope",
            HttpStatus.NOT_FOUND,
            0X1771,
            errorMap);
      }
      return null;
    }

    return INSTANCE.toSourcingRuleAttributesDefinitionResponse(
        sourcingAttributesDefinitionDomainDtoList.get(0));
  }

  public SourcingAttributeDefinitionUIResponse processGetActiveSourcingAttributeDefinitionForUI(
      String orgId, SourcingAttributesDefinitionScopeEnum scope)
      throws CommonServiceException, PromiseEngineException {
    if (Objects.isNull(scope)) {
      scope = SourcingAttributesDefinitionScopeEnum.SOURCING_RULE;
    }
    List<SourcingAttributesDefinitionDomainDto> sourcingAttributesDefinitionDomainDtoList =
        sourcingAttributesDefinitionPersistenceService
            .fetchSourcingRuleAttributesDefinitionListByOrgIdAndStatusAndScope(
                orgId, SourcingAttributesDefinitionStatus.ACTIVE, scope);
    if (sourcingAttributesDefinitionDomainDtoList.isEmpty()) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SCOPE, FieldError.builder().rejectedValue(scope).build());
      throw new CommonServiceException(
          "No active attributes definition exists for given orgId and " + scope,
          HttpStatus.NOT_FOUND,
          0X1771,
          errorMap);
    }

    SourcingAttributesDefinitionDomainDto entity = sourcingAttributesDefinitionDomainDtoList.get(0);
    List<AttributeInfo> requiredAttributes = new ArrayList<>();
    List<AttributeInfo> optionalAttributes = new ArrayList<>();

    List<String> requiredAttributeIds =
        StringUtils.hasLength(entity.getReqAttributes())
            ? List.of(entity.getReqAttributes().split(","))
            : new ArrayList<>();
    List<String> optionalAttributeIds =
        StringUtils.hasLength(entity.getOptAttributes())
            ? List.of(entity.getOptAttributes().split(","))
            : new ArrayList<>();

    populateReqAndOptAttributes(
        orgId, requiredAttributes, optionalAttributes, requiredAttributeIds, optionalAttributeIds);
    SourcingAttributeDefinitionUIResponse response = new SourcingAttributeDefinitionUIResponse();
    response.setOrgId(orgId);
    response.setSourcingAttributesDefinitionId(entity.getId().toString());
    response.setRequiredAttributes(requiredAttributes);
    response.setOptionalAttributes(optionalAttributes);
    return response;
  }

  private void populateReqAndOptAttributes(
      String orgId,
      List<AttributeInfo> requiredAttributes,
      List<AttributeInfo> optionalAttributes,
      List<String> requiredAttributeIds,
      List<String> optionalAttributeIds)
      throws PromiseEngineException, CommonServiceException {
    for (String requiredAttributeId : requiredAttributeIds) {
      findAttributeDetailsFromAttributeId(orgId, requiredAttributes, requiredAttributeId);
    }

    for (String optionalAttributeId : optionalAttributeIds) {
      findAttributeDetailsFromAttributeId(orgId, optionalAttributes, optionalAttributeId);
    }
  }

  private void findAttributeDetailsFromAttributeId(
      String orgId, List<AttributeInfo> requiredAttributes, String requiredAttributeId)
      throws PromiseEngineException, CommonServiceException {
    Optional<SourcingAttributeDomainDto> sourcingAttributeDomainDtoOptional =
        sourcingAttributePersistenceService.getSourcingAttributeById(
            Long.parseLong(requiredAttributeId));
    if (sourcingAttributeDomainDtoOptional.isPresent()) {
      SourcingAttributeDomainDto sourcingAttributeDomainDto =
          sourcingAttributeDomainDtoOptional.get();
      AttributeInfo attributeInfo =
          new AttributeInfo(
              sourcingAttributeDomainDto.getAttributeName(),
              sourcingAttributeDomainDto.getId().toString(),
              null);
      requiredAttributes.add(attributeInfo);
    } else {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(ATTRIBUTE_ID, FieldError.builder().rejectedValue(requiredAttributeId).build());
      throw new CommonServiceException(
          "No attribute entity found for given orgId and attribute id",
          HttpStatus.NOT_FOUND,
          0X1771,
          errorMap);
    }
  }
}
