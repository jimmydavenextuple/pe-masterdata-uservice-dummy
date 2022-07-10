package com.hbc.node.carrier.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hbc.core.cache.domain.CacheValue;
import com.hbc.core.cache.service.GenericFeignCacheService;
import com.hbc.core.constants.NearCacheConstants;
import com.hbc.core.registry.NearCacheRegistry;
import com.hbc.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheKey;
import com.hbc.node.carrier.calendar.cache.domain.NodeCarrierCalendarCacheValue;
import com.hbc.node.carrier.calendar.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierCalendarNearCacheServiceImplTest {
  @InjectMocks
  private NodeCarrierCalendarSpringNearCacheService nodeCarrierCalendarSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock
  private GenericFeignCacheService<NodeCarrierCalendarCacheKey, NodeCarrierCalendarCacheValue>
      feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;

  @Test
  void getValidTest() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();
    NodeCarrierCalendarCacheValue cacheValue = testUtil.getNodeCarrierCalendarCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = nodeCarrierCalendarSpringNearCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);

    // Second Invocation
    CacheValue cacheValue2 = abstractGenericFeignClientService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);

    // Third Invocation
    CacheValue cacheValue3 = abstractGenericFeignClientService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getInValidTest() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeCarrierCalendarSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

    nodeCarrierCalendarSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = nodeCarrierCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    NodeCarrierCalendarCacheKey cacheKey = testUtil.getNodeCarrierCalendarCacheKey();

    nodeCarrierCalendarSpringNearCacheService.deleteAll();
    CacheValue cacheValue = nodeCarrierCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegisterTest() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    nodeCarrierCalendarSpringNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.NODE_CARRIER_CALENDAR_ENTITY_NAME,
        nodeCarrierCalendarSpringNearCacheService.getEntityName());
  }
}
