package com.nextuple.pe.light.promise.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nextuple.common.exception.HardExecutionFailureException;
import com.nextuple.common.exception.ServiceUnavailableException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.light.promise.service.impl.CacheWarmUpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CacheWarmupControllerTest {

  private MockMvc mockMvc;
  @InjectMocks CacheWarmupController controller;

  @Mock private CacheWarmUpService cacheWarmUpService;

  @InjectMocks private CacheWarmupController cacheWarmupController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(cacheWarmupController).build();
  }

  @Test
  @DisplayName("Test successful cache eviction")
  void evictNearCache_Success() throws Exception {
    doNothing().when(cacheWarmUpService).deleteAllNearCacheData();
    mockMvc
        .perform(delete("/evict-cache").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Near cache values evicted successfully!"));

    verify(cacheWarmUpService, times(1)).deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test cache eviction with generic Exception")
  void evictNearCache_GenericException() throws Exception {
    doThrow(new RuntimeException("Generic error"))
        .when(cacheWarmUpService)
        .deleteAllNearCacheData();

    mockMvc
        .perform(delete("/evict-cache").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(
            jsonPath("$.message")
                .value("Some error in performing near cache eviction Reason: Generic error"));

    verify(cacheWarmUpService, times(1)).deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test direct controller method call for successful cache eviction")
  void evictNearCache_DirectCall_Success() {
    doNothing().when(cacheWarmUpService).deleteAllNearCacheData();

    var response = cacheWarmupController.evictNearCache();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Near cache values evicted successfully!", response.getBody().getMessage());
    verify(cacheWarmUpService, times(1)).deleteAllNearCacheData();
  }

  @Test
  @DisplayName("Test direct controller method call for generic exception")
  void evictNearCache_DirectCall_GenericException() {
    doThrow(new RuntimeException("Generic error"))
        .when(cacheWarmUpService)
        .deleteAllNearCacheData();

    var response = cacheWarmupController.evictNearCache();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(
        "Some error in performing near cache eviction Reason: Generic error",
        response.getBody().getMessage());
    verify(cacheWarmUpService, times(1)).deleteAllNearCacheData();
  }

  @Test
  void evictNearCacheTest() {
    doNothing().when(cacheWarmUpService).deleteAllNearCacheData();

    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  void evictNearCacheExceptionTest() {
    doThrow(new RuntimeException()).when(cacheWarmUpService).deleteAllNearCacheData();

    ResponseEntity<BaseResponse<String>> response = controller.evictNearCache();
    Assertions.assertNotNull(response);
    Assertions.assertEquals(500, response.getStatusCodeValue());
  }

  @Test
  @DisplayName("Evict near cache with service unavailable exception")
  void evictNearCacheServiceUnavailableExceptionTest() {
    doThrow(new ServiceUnavailableException()).when(cacheWarmUpService).deleteAllNearCacheData();
    Assertions.assertThrows(ServiceUnavailableException.class, () -> controller.evictNearCache());
  }

  @Test
  @DisplayName("Evict near cache with hard execution failure exception")
  void evictNearCacheHardExecutionFailureExceptionTest() {
    doThrow(new HardExecutionFailureException()).when(cacheWarmUpService).deleteAllNearCacheData();
    Assertions.assertThrows(HardExecutionFailureException.class, () -> controller.evictNearCache());
  }
}
