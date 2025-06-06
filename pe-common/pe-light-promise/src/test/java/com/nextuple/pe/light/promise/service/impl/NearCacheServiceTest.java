/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.core.cache.service.GenericNearCacheService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NearCacheServiceTest {

  @InjectMocks private NearCacheServiceImpl nearCacheService;

  @Mock private GenericNearCacheService<?, ?> mockCacheService1;
  @Mock private GenericNearCacheService<?, ?> mockCacheService2;
  private List<GenericNearCacheService<?, ?>> nearCacheServices;

  @BeforeEach
  void setup() {
    nearCacheServices = new ArrayList<>();
    nearCacheServices.add(mockCacheService1);
    nearCacheServices.add(mockCacheService2);
    ReflectionTestUtils.setField(nearCacheService, "nearCacheServices", nearCacheServices);
  }

  @Test
  @DisplayName("Test successful deletion of all near cache data")
  void testDeleteAllNearCacheDataSuccess() throws CommonServiceException {
    nearCacheService.deleteAllNearCacheData();
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Test deletion with empty cache services list")
  void testDeleteAllNearCacheDataEmptyList() throws CommonServiceException {
    List<GenericNearCacheService<?, ?>> emptyList = new ArrayList<>();
    ReflectionTestUtils.setField(nearCacheService, "nearCacheServices", emptyList);
    nearCacheService.deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test exception handling when a cache service fails to delete")
  void testDeleteAllNearCacheDataWithException() {
    RuntimeException mockException = new RuntimeException("Delete failed");
    doThrow(mockException).when(mockCacheService1).deleteAll();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> {
              nearCacheService.deleteAllNearCacheData();
            });

    assertEquals("Error while deleting near cache data", exception.getMessage());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    assertEquals(0x2003, exception.getErrorCode());
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(0)).deleteAll();
  }
}
