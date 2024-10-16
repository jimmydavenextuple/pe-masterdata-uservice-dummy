/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static com.nextuple.sourcing.cost.config.utils.SourcingCostConfigUtil.REGEX_PATTERN_FOR_FORMULA_VALIDATION;
import static com.nextuple.sourcing.cost.config.utils.SourcingCostConfigUtil.isPrimitive;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.sourcing.cost.config.customannotations.AttributePath;
import com.nextuple.sourcing.cost.config.domain.configuration.PostConstructValidationBean;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeMappingEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.enums.ExpressionLibraryEnum;
import com.nextuple.sourcing.cost.config.enums.LookupContextEnum;
import com.nextuple.sourcing.cost.config.inbound.ExpressionValidationRequest;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingRequestForFormulaValidation;
import com.nextuple.sourcing.cost.config.pojo.SampleSourcingSolutionForFormulaValidation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressionValidationService {

  public static final String ATTRIBUTE_VALUE = "attributeValue";
  public static final String ATTRIBUTE_PATH = "attributePath";
  public static final String ORG_ID = "orgId";
  public static final String LOOKUP_CONTEXT = "lookupContext";
  public static final String FORMULA_VALIDATION_EXCEPTION =
      "Expecting numeric values for formula validation";
  public static final String ATTRIBUTE_PATH_STAR = "*";
  public static final String ATTRIBUTE_PATH_REPLACEMENT = "0";
  public static final String LIBRARY_NOT_SUPPORTED_EXCEPTION =
      "Passed library is not not supported";
  public static final String SERIAL_VERSION_UID = "serialVersionUID";
  public static final String LIBRARY_NAME = "libraryName";
  public static final String CONTEXT_MAP_EXCEPTION = "Exception while creating context map : ";
  public static final String FIELD_KEY = "field";
  public static final String OBJECT_KEY = "object";
  public static final String CUSTOM_ATTRIBUTES_REGEX = "customAttributes.";
  public static final String CUSTOM_ATTRIBUTES = "customAttributes";
  private final PostConstructValidationBean postConstructValidationBean;

  private final CostAttributeMappingRepository costAttributeMappingRepository;

  private final CostAttributeRepository costAttributeRepository;

  private final ParsiiValidationLibraryServiceImpl parsiiValidationLibraryService;

  private final SpelValidationLibraryServiceImpl spelValidationLibraryService;

  public ExpressionValidationResponse validateExpression(
      String orgId, String libraryName, ExpressionValidationRequest expressionValidationRequest)
      throws CommonServiceException {

    // get pre-loaded sample request from resource json
    SampleSourcingRequestForFormulaValidation sampleSourcingRequestForFormulaValidation =
        postConstructValidationBean.getSampleSourcingRequestForFormulaValidation();

    // get pre-loaded sample solution from resource json
    SampleSourcingSolutionForFormulaValidation sampleSourcingSolutionForFormulaValidation =
        postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation();

    Map<String, Object> sampleRequestContextMap = new HashMap<>();
    Map<String, Object> sampleSolutionContextMap = new HashMap<>();

    // convert pre-loaded sample request from resource json to request context map
    processObjectFields(sampleSourcingRequestForFormulaValidation, "", sampleRequestContextMap);

    // convert pre-loaded sample solution from resource json to solution context map
    processObjectFields(sampleSourcingSolutionForFormulaValidation, "", sampleSolutionContextMap);

    // convert sample request from request body to map and update  request context map
    checkAndUpdateContextMap(
        expressionValidationRequest.getSampleRequest(), sampleRequestContextMap);

    // convert sample solution from request body to map and update solution context map
    checkAndUpdateContextMap(
        expressionValidationRequest.getSampleSolution(), sampleSolutionContextMap);

    // Validate and evaluate formula expression
    Double expressionValue =
        validateAndGetExpressionValue(
            orgId,
            libraryName,
            expressionValidationRequest.getExpression(),
            sampleRequestContextMap,
            sampleSolutionContextMap);

    return ExpressionValidationResponse.builder()
        .expressionValue(expressionValue)
        .sampleSolution(sampleSourcingSolutionForFormulaValidation)
        .sampleRequest(sampleSourcingRequestForFormulaValidation)
        .build();
  }

  private Double validateAndGetExpressionValue(
      String orgId,
      String libraryName,
      String expression,
      Map<String, Object> sampleRequestContextMap,
      Map<String, Object> sampleSolutionContextMap)
      throws CommonServiceException {
    Pattern pattern = REGEX_PATTERN_FOR_FORMULA_VALIDATION;
    Matcher matcher = pattern.matcher(expression);
    Map<String, Object> attributeValueMap = new HashMap<>();
    Map<String, Boolean> attributeScopeMap = new HashMap<>();

    while (matcher.find()) {
      String inputKey = matcher.group();

      attributeValueMap.put(inputKey, inputKey);
      attributeScopeMap.put(inputKey, Boolean.FALSE);

      Object attributeValue = null;
      if (inputKey.startsWith(CUSTOM_ATTRIBUTES_REGEX)) {
        String customAttributeKey = inputKey.split("\\.")[1];
        String customAttributeHeaderLevelPath = "/customAttributes/" + customAttributeKey;
        String customAttributeOrderLineLevelPath =
            "/orderLines/0/customAttributes/" + customAttributeKey;
        attributeValue = sampleRequestContextMap.get(customAttributeOrderLineLevelPath);
        if (attributeValue == null)
          attributeValue = sampleRequestContextMap.get(customAttributeHeaderLevelPath);
        if (attributeValue == null) attributeValue = 1.0;
        inputKey = inputKey.replace(".", "");
        expression = expression.replace(CUSTOM_ATTRIBUTES_REGEX, CUSTOM_ATTRIBUTES);
        attributeValueMap.put(inputKey, attributeValue);
        attributeScopeMap.put(inputKey, Boolean.TRUE);

      } else {
        // Check if given attribute present in cost attribute details table
        Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
            getCostAttributeDetailsEntity(orgId, inputKey);

        if (costAttributeDetailsEntity.isPresent()) {

          String attributePath = costAttributeDetailsEntity.get().getPath();
          attributePath = attributePath.replace(ATTRIBUTE_PATH_STAR, ATTRIBUTE_PATH_REPLACEMENT);

          LookupContextEnum lookupContext = costAttributeDetailsEntity.get().getLookupContext();

          if (lookupContext.equals(LookupContextEnum.SOLUTION)) {
            attributeValue = sampleSolutionContextMap.get(attributePath);
          } else {
            attributeValue = sampleRequestContextMap.get(attributePath);
          }

          if (!isNumeric(attributeValue)) {
            log.error(FORMULA_VALIDATION_EXCEPTION);
            Map<String, FieldError> errorMap = new HashMap<>();
            errorMap.put(
                ATTRIBUTE_VALUE, FieldError.builder().rejectedValue(attributeValue).build());
            errorMap.put(ATTRIBUTE_PATH, FieldError.builder().rejectedValue(attributePath).build());
            errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
            errorMap.put(LOOKUP_CONTEXT, FieldError.builder().rejectedValue(lookupContext).build());
            throw new CommonServiceException(
                FORMULA_VALIDATION_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
          }

          attributeValueMap.put(inputKey, attributeValue);
          attributeScopeMap.put(inputKey, Boolean.TRUE);
        }
      }
    }

    Double expressionValue =
        validateExpressionViaLibrary(libraryName, attributeValueMap, attributeScopeMap, expression);
    return expressionValue;
  }

  private Optional<CostAttributeDetailsEntity> getCostAttributeDetailsEntity(
      String orgId, String inputKey) {
    String keyToGetPath = inputKey;
    List<CostAttributeMappingEntity> costAttributeMappingEntities =
        costAttributeMappingRepository.findByOrgIdAndCanonicalName(orgId, keyToGetPath);
    if (!costAttributeMappingEntities.isEmpty()) {
      keyToGetPath = costAttributeMappingEntities.get(0).getAttributeName();
    }

    Optional<CostAttributeDetailsEntity> costAttributeDetailsEntity =
        costAttributeRepository.findByAttributeName(keyToGetPath);
    return costAttributeDetailsEntity;
  }

  private Double validateExpressionViaLibrary(
      String libraryName,
      Map<String, Object> attributeValueMap,
      Map<String, Boolean> attributeScopeMap,
      String expression)
      throws CommonServiceException {

    if (libraryName.equals(ExpressionLibraryEnum.PARSII.getLibraryName())) {
      return parsiiValidationLibraryService.validateFormula(
          expression, attributeValueMap, attributeScopeMap);
    } else if (libraryName.equals(ExpressionLibraryEnum.SPEL.getLibraryName())) {

      return spelValidationLibraryService.validateFormula(
          expression, attributeValueMap, attributeScopeMap);
    } else {
      log.error(LIBRARY_NOT_SUPPORTED_EXCEPTION);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(LIBRARY_NAME, FieldError.builder().rejectedValue(libraryName).build());
      throw new CommonServiceException(
          LIBRARY_NOT_SUPPORTED_EXCEPTION, HttpStatus.BAD_REQUEST, 0x1771, errorMap);
    }
  }

  private boolean isNumeric(Object value) {
    return Optional.ofNullable(value)
        .map(Object::toString)
        .filter(str -> !str.trim().isEmpty())
        .map(
            str -> {
              try {
                Double.parseDouble(str);
                return true;
              } catch (NumberFormatException e) {
                return false;
              }
            })
        .orElse(false);
  }

  private void checkAndUpdateContextMap(Object sourceObject, Map<String, Object> mapToUpdate)
      throws CommonServiceException {
    if (sourceObject != null) {
      Map<String, Object> tempMap = new HashMap<>();
      // convert sourceObject to map
      processObjectFields(sourceObject, "", tempMap);
      // update mapToUpdate using generated tempMap
      updateContextMap(mapToUpdate, tempMap);
    }
  }

  private void updateContextMap(Map<String, Object> toMap, Map<String, Object> fromMap) {
    for (Map.Entry<String, Object> entry : fromMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value != null && !value.toString().isBlank()) {
        toMap.put(key, value);
      }
    }
  }

  void processObjectFields(Object obj, String currentPath, Map<String, Object> contextMap)
      throws CommonServiceException {
    if (obj == null) {
      return; // Early return for null object
    }

    for (Field field : obj.getClass().getDeclaredFields()) {
      try {
        if (field.getName().equals(SERIAL_VERSION_UID)) {
          continue; // Skip serialVersionUID field
        }

        field.setAccessible(true);
        String fieldPath = field.getAnnotation(AttributePath.class).path();
        Object fieldValue = field.get(obj);

        Type fieldType = field.getGenericType();

        if (isPrimitive(fieldType)) {
          contextMap.put(currentPath + fieldPath, fieldValue);
        } else if (fieldType instanceof ParameterizedType type) {
          processParameterizedField(fieldValue, currentPath, fieldPath, contextMap, type);
        } else {
          processObjectFields(fieldValue, currentPath + fieldPath, contextMap);
        }
      } catch (Exception e) {
        log.error(
            CONTEXT_MAP_EXCEPTION + "while processing field '{}' in object '{}': {}",
            field.getName(),
            obj.getClass().getName(),
            e.getMessage());
        Map<String, FieldError> errorMap = new HashMap<>();
        errorMap.put(FIELD_KEY, FieldError.builder().rejectedValue(field.getName()).build());
        errorMap.put(
            OBJECT_KEY, FieldError.builder().rejectedValue(obj.getClass().getName()).build());
        throw new CommonServiceException(
            CONTEXT_MAP_EXCEPTION + e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            0x1771,
            errorMap);
      }
    }
  }

  void processParameterizedField(
      Object fieldValue,
      String currentPath,
      String fieldPath,
      Map<String, Object> contextMap,
      ParameterizedType parameterizedType)
      throws CommonServiceException {
    if (parameterizedType.getRawType().equals(List.class)) {
      if (isPrimitive(parameterizedType.getActualTypeArguments()[0])) {
        contextMap.put(currentPath + fieldPath, fieldValue);
      } else {
        processListField((List<?>) fieldValue, currentPath, fieldPath, contextMap);
      }
    } else if (parameterizedType.getRawType().equals(Map.class)) {
      processMapField((Map<String, ?>) fieldValue, currentPath, fieldPath, contextMap);
    }
  }

  void processListField(
      List<?> fieldObjectList, String currentPath, String fieldPath, Map<String, Object> contextMap)
      throws CommonServiceException {
    if (fieldObjectList != null) {
      for (int i = 0; i < fieldObjectList.size(); i++) {
        String updatedListPath = fieldPath.replace(ATTRIBUTE_PATH_STAR, String.valueOf(i));
        processObjectFields(fieldObjectList.get(i), currentPath + updatedListPath, contextMap);
      }
    }
  }

  void processMapField(
      Map<String, ?> fieldObjectMap,
      String currentPath,
      String fieldPath,
      Map<String, Object> contextMap) {
    if (fieldObjectMap != null) {
      for (Map.Entry<String, ?> entry : fieldObjectMap.entrySet()) {
        contextMap.put(currentPath + fieldPath + entry.getKey(), entry.getValue());
      }
    }
  }
}
