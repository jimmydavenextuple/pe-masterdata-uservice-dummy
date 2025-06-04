package com.nextuple.pe.light.promise.service.impl;

import static org.mockito.Mockito.*;

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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CacheWarmupServiceTest {

  @Mock private GenericNearCacheService<?, ?> mockCacheService1;

  @Mock private GenericNearCacheService<?, ?> mockCacheService2;

  @InjectMocks private CacheWarmUpService cacheWarmUpService;

  private List<GenericNearCacheService<?, ?>> nearCacheServices;

  @BeforeEach
  public void setup() {
    nearCacheServices = new ArrayList<>();
    nearCacheServices.add(mockCacheService1);
    nearCacheServices.add(mockCacheService2);
    ReflectionTestUtils.setField(cacheWarmUpService, "nearCacheServices", nearCacheServices);
  }

  @Test
  @DisplayName("Test successful deletion of all near cache data")
  public void testDeleteAllNearCacheData_Success() {
    cacheWarmUpService.deleteAllNearCacheData();
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Test deletion when one cache service throws an exception")
  public void testDeleteAllNearCacheData_WithException() {
    doThrow(new RuntimeException("Test Exception")).when(mockCacheService1).deleteAll();
    when(mockCacheService1.getEntityName()).thenReturn("TestCache1");
    cacheWarmUpService.deleteAllNearCacheData();
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Test deletion with empty cache services list")
  public void testDeleteAllNearCacheData_EmptyList() {
    List<GenericNearCacheService<?, ?>> emptyList = new ArrayList<>();
    ReflectionTestUtils.setField(cacheWarmUpService, "nearCacheServices", emptyList);
    cacheWarmUpService.deleteAllNearCacheData();
  }
}
