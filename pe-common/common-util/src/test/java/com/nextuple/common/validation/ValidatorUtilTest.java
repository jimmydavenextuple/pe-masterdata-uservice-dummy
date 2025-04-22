/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.common.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidatorUtilTest {

  private Validator validator;
  private ValidatorUtil validatorUtil;

  @BeforeEach
  void setUp() {
    validator = mock(Validator.class);
    validatorUtil = new ValidatorUtil(validator);
  }

  @Test
  @DisplayName("Should pass validation when no constraint violations found")
  void validateWithGroupShouldPass() {
    Object testObject = new Object();
    when(validator.validate(testObject, Default.class)).thenReturn(Collections.emptySet());

    validatorUtil.validateWithGroup(testObject, Default.class);

    verify(validator, times(1)).validate(testObject, Default.class);
  }

  @Test
  @DisplayName("Should throw ConstraintViolationException when violations found")
  void validateWithGroupShouldThrowException() {
    Object testObject = new Object();

    ConstraintViolation<Object> mockViolation = mock(ConstraintViolation.class);
    Set<ConstraintViolation<Object>> violations = Set.of(mockViolation);

    when(validator.validate(testObject, Default.class)).thenReturn(violations);

    assertThrows(
        ConstraintViolationException.class,
        () -> validatorUtil.validateWithGroup(testObject, Default.class));

    verify(validator, times(1)).validate(testObject, Default.class);
  }
}
