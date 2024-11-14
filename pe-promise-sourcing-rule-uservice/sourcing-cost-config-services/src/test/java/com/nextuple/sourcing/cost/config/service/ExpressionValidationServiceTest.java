/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.configuration.PostConstructValidationBean;
import com.nextuple.sourcing.cost.config.domain.entity.CostAttributeDetailsEntity;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeMappingRepository;
import com.nextuple.sourcing.cost.config.domain.repository.CostAttributeRepository;
import com.nextuple.sourcing.cost.config.inbound.ExpressionValidationRequest;
import com.nextuple.sourcing.cost.config.outbound.ExpressionValidationResponse;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class ExpressionValidationServiceTest {

  public static final String LENGTH_WITH_INTEGER_VALUES = "length002";
  public static final String WIDTH_WITH_INTEGER_VALUES = "width352";
  @Mock private PostConstructValidationBean postConstructValidationBean;

  @Mock private CostAttributeMappingRepository costAttributeMappingRepository;

  @Mock private CostAttributeRepository costAttributeRepository;

  @Mock ParsiiValidationLibraryServiceImpl parsiiValidationLibraryService;

  @Mock SpelValidationLibraryServiceImpl spelValidationLibraryService;

  @InjectMocks private ExpressionValidationService expressionValidationService;

  @InjectMocks private TestUtil testUtil;

  @Captor ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void validateExpressionSpelTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();
    costAttributeDetailsEntity.setPath("/shipments/0/items/0/width");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, TestUtil.LIBRARY_NAME, testUtil.getExpressionValidationRequest());

    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());

    verify(spelValidationLibraryService, times(1)).validateFormula(anyString(), any(), any());
    verify(parsiiValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(costAttributeMappingRepository, times(2))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeRepository, times(2)).findByAttributeName(anyString());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionSpelForCustomAttributesTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());
    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());
    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));

    ExpressionValidationRequest expressionValidationRequest =
        testUtil.getExpressionValidationRequest();
    expressionValidationRequest.setExpression("customAttributes.surge*100");
    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, TestUtil.LIBRARY_NAME, expressionValidationRequest);
    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());

    verify(spelValidationLibraryService, times(1)).validateFormula(anyString(), any(), any());
    verify(parsiiValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionSpelVaraibleWithIntegersTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();
    costAttributeDetailsEntity.setPath("/shipments/0/items/0/width");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    ExpressionValidationRequest expressionValidationRequest =
        testUtil.getExpressionValidationRequest();
    expressionValidationRequest.setExpression(
        LENGTH_WITH_INTEGER_VALUES + "*" + WIDTH_WITH_INTEGER_VALUES);

    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, TestUtil.LIBRARY_NAME, expressionValidationRequest);

    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());
    verify(parsiiValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(spelValidationLibraryService, times(1))
        .validateFormula(anyString(), mapArgumentCaptor.capture(), any());
    Map<String, Object> capturedMap = mapArgumentCaptor.getValue();
    assertTrue(capturedMap.containsKey(LENGTH_WITH_INTEGER_VALUES));
    assertTrue(capturedMap.containsKey(WIDTH_WITH_INTEGER_VALUES));
    verify(costAttributeMappingRepository, times(2))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeRepository, times(2)).findByAttributeName(anyString());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionParsiTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();
    costAttributeDetailsEntity.setPath("/shipments/0/items/0/width");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, "parsii", testUtil.getExpressionValidationRequest());

    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());

    verify(spelValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(parsiiValidationLibraryService, times(1)).validateFormula(anyString(), any(), any());
    verify(costAttributeMappingRepository, times(2))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeRepository, times(2)).findByAttributeName(anyString());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionParsiiForCustomAttributesTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    ExpressionValidationRequest expressionValidationRequest =
        testUtil.getExpressionValidationRequest();
    expressionValidationRequest.setExpression("customAttributes.surge*100");
    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, "parsii", expressionValidationRequest);

    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());

    verify(spelValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(parsiiValidationLibraryService, times(1)).validateFormula(anyString(), any(), any());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionParsiVaraibleWithIntegersTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();
    costAttributeDetailsEntity.setPath("/shipments/0/items/0/width");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    ExpressionValidationRequest expressionValidationRequest =
        testUtil.getExpressionValidationRequest();
    expressionValidationRequest.setExpression(
        LENGTH_WITH_INTEGER_VALUES + "*" + WIDTH_WITH_INTEGER_VALUES);

    ExpressionValidationResponse response =
        expressionValidationService.validateExpression(
            TestUtil.ORG_ID, "parsii", expressionValidationRequest);

    assertEquals(TestUtil.EXPRESSION_VALUE, response.getExpressionValue());
    assertEquals("NEXTUPLE_GR", response.getSampleRequest().getOrgId());

    verify(spelValidationLibraryService, times(0)).validateFormula(anyString(), any(), any());
    verify(parsiiValidationLibraryService, times(1))
        .validateFormula(anyString(), mapArgumentCaptor.capture(), any());

    Map<String, Object> capturedMap = mapArgumentCaptor.getValue();
    assertTrue(capturedMap.containsKey(LENGTH_WITH_INTEGER_VALUES));
    assertTrue(capturedMap.containsKey(WIDTH_WITH_INTEGER_VALUES));
    verify(costAttributeMappingRepository, times(2))
        .findByOrgIdAndCanonicalName(anyString(), anyString());
    verify(costAttributeRepository, times(2)).findByAttributeName(anyString());
    verify(postConstructValidationBean, times(1)).getSampleSourcingRequestForFormulaValidation();
    verify(postConstructValidationBean, times(1)).getSampleSourcingSolutionForFormulaValidation();
  }

  @Test
  void validateExpressionNonParsiExceptionTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenThrow(CommonServiceException.class);

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();
    costAttributeDetailsEntity.setPath("/shipments/0/items/0/width");
    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                expressionValidationService.validateExpression(
                    TestUtil.ORG_ID, "unKnownLibrary", testUtil.getExpressionValidationRequest()));

    assertEquals("Passed library is not not supported", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey("libraryName"));
  }

  @Test
  void validateExpressionNonNumericValueTest() throws CommonServiceException {
    when(postConstructValidationBean.getSampleSourcingRequestForFormulaValidation())
        .thenReturn(testUtil.getSampleRequest());
    when(postConstructValidationBean.getSampleSourcingSolutionForFormulaValidation())
        .thenReturn(testUtil.getSampleSolution());

    when(parsiiValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(spelValidationLibraryService.validateFormula(anyString(), any(), any()))
        .thenReturn(testUtil.getSampleSolution().getTotalCost());

    when(costAttributeMappingRepository.findByOrgIdAndCanonicalName(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getCostAttributeMappingEntity()));
    CostAttributeDetailsEntity costAttributeDetailsEntity =
        testUtil.getCostAttributeDetailsEntity();

    when(costAttributeRepository.findByAttributeName(anyString()))
        .thenReturn(Optional.ofNullable(costAttributeDetailsEntity));

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                expressionValidationService.validateExpression(
                    TestUtil.ORG_ID, "parsii", testUtil.getExpressionValidationRequest()));

    assertEquals(TestUtil.FORMULA_VALIDATION_EXCEPTION, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ORG_ID_KEY));
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.LOOKUP_CONTEXT));
    assertTrue(exception.getFieldInfo().containsKey(TestUtil.ATTRIBUTE_VALUE));
  }
}
