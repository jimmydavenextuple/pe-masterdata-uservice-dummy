package com.nextuple.pe.light.promise.service.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class NearCacheServiceTest {

  @Mock private GenericNearCacheService<?, ?> mockCacheService1;

  @Mock private GenericNearCacheService<?, ?> mockCacheService2;

  @InjectMocks private NearCacheService nearCacheService;

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
  void testDeleteAllNearCacheDataSuccess() {
    nearCacheService.deleteAllNearCacheData();
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Test deletion when one cache service throws an exception")
  void testDeleteAllNearCacheDataWithException() {
    doThrow(new RuntimeException("Test Exception")).when(mockCacheService1).deleteAll();
    when(mockCacheService1.getEntityName()).thenReturn("TestCache1");
    nearCacheService.deleteAllNearCacheData();
    verify(mockCacheService1, times(1)).deleteAll();
    verify(mockCacheService2, times(1)).deleteAll();
  }

  @Test
  @DisplayName("Test deletion with empty cache services list")
  void testDeleteAllNearCacheDataEmptyList() {
    List<GenericNearCacheService<?, ?>> emptyList = new ArrayList<>();
    ReflectionTestUtils.setField(nearCacheService, "nearCacheServices", emptyList);
    nearCacheService.deleteAllNearCacheData();
  }
}
