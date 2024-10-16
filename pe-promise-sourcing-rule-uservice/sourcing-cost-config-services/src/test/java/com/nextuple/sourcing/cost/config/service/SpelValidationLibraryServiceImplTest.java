/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.sourcing.cost.config.domain.configuration.PostConstructValidationBean;
import com.nextuple.sourcing.cost.config.utils.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class SpelValidationLibraryServiceImplTest {

  @Mock private PostConstructValidationBean postConstructValidationBean;

  @InjectMocks private TestUtil testUtil;

  @InjectMocks private SpelValidationLibraryServiceImpl spelValidationLibraryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void validateFormulaTest() throws CommonServiceException, NoSuchMethodException {

    when(postConstructValidationBean.getSpelStandardEvaluationContext())
        .thenReturn(testUtil.getSpelContext());

    Double formulaValue =
        spelValidationLibraryService.validateFormula(
            "l*b*h", testUtil.getAlgoValueMap(), testUtil.getSetScope());

    assertEquals(6000, formulaValue);
  }

  @Test
  void validateFormulaExceptionTest() {
    Map<String, Object> algoMap = testUtil.getAlgoValueMap();
    algoMap.put("l", "invalidValue");

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () ->
                spelValidationLibraryService.validateFormula(
                    "l*b*h", algoMap, testUtil.getSetScope()));

    assertEquals("Exception while parsing formula via spel ", exception.getMessage().split(":")[0]);
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(0x1771, exception.getErrorCode());
    assertTrue(exception.getFieldInfo().containsKey("libraryName"));
    assertTrue(exception.getFieldInfo().containsKey("formulaExpression"));
  }
}
