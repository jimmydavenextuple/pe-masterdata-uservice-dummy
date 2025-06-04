package com.nextuple.pe.light.promise.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.light.promise.service.impl.NearCacheService;
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
        "Some error in performing near cache eviction Reason: Generic error",
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
    doThrow(new RuntimeException()).when(nearCacheService).deleteAllNearCacheData();

    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  @Test
  @DisplayName("Evict near cache with service unavailable exception")
  void evictNearCacheServiceUnavailableExceptionTest() {
    doThrow(new ServiceUnavailableException()).when(nearCacheService).deleteAllNearCacheData();
    Assertions.assertThrows(ServiceUnavailableException.class, () -> controller.evictNearCache());
  }

  @Test
  @DisplayName("Evict near cache with hard execution failure exception")
  void evictNearCacheHardExecutionFailureExceptionTest() {
    doThrow(new HardExecutionFailureException()).when(nearCacheService).deleteAllNearCacheData();
    Assertions.assertThrows(HardExecutionFailureException.class, () -> controller.evictNearCache());
  }
}
