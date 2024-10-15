/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.item.persistence.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class ItemExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks ItemExceptionHandler itemExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling item domain exception")
  void handleItemDomainException() {
    ItemDomainException exception =
        new ItemDomainException(
            "Internal Server Error", TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        itemExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling item batching domain exception")
  void handleItemBatchingDomainException() {
    ItemBatchingDomainException exception =
        new ItemBatchingDomainException(
            "Internal Server Error", List.of(TestUtil.ITEM_ID), TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        itemExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
