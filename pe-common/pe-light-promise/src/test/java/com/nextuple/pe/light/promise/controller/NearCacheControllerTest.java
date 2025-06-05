/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.pe.light.promise.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.light.promise.service.NearCacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class NearCacheControllerTest {

  @InjectMocks NearCacheController controller;

  @Mock private NearCacheService nearCacheService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test successful cache eviction")
  void evictNearCacheSuccess() {
    doNothing().when(nearCacheService).deleteAllNearCacheData();
    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Near cache values evicted successfully!", response.getBody().getMessage());
    verify(nearCacheService, times(1)).deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test cache eviction with generic Exception")
  void evictNearCacheGenericException() {
    doThrow(new RuntimeException("Generic error")).when(nearCacheService).deleteAllNearCacheData();
    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(
        "Error in performing near cache eviction Reason: Generic error",
        response.getBody().getMessage());
    verify(nearCacheService, times(1)).deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test successful cache eviction with status code check")
  void evictNearCacheTest() {
    doNothing().when(nearCacheService).deleteAllNearCacheData();
    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  @DisplayName("Test cache eviction with RuntimeException")
  void evictNearCacheExceptionTest() {
    doThrow(new RuntimeException("Test exception")).when(nearCacheService).deleteAllNearCacheData();
    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    Assertions.assertEquals(
        "Error in performing near cache eviction Reason: Test exception",
        response.getBody().getMessage());
  }
}
