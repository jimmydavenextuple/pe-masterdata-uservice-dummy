package com.nextuple.node.calendar.cache.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheKey;
import com.nextuple.node.calendar.cache.domain.NodeCalendarCacheValue;
import com.nextuple.node.calendar.cache.spring.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCalendarSpringNearCacheServiceImplTest {
  @InjectMocks private NodeCalendarSpringNearCacheService nodeCalendarSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private AbstractGenericFeignClientServiceImpl abstractGenericFeignClientService;

  @Mock
  private GenericFeignCacheService<NodeCalendarCacheKey, NodeCalendarCacheValue> feignCacheService;

  @Mock private NearCacheRegistry nearCacheRegistry;

  @Test
  void getValidTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();
    NodeCalendarCacheValue cacheValue = testUtil.getNodeCalendarCacheValue();

    when(feignCacheService.get(any())).thenReturn(cacheValue);
    when(abstractGenericFeignClientService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = nodeCalendarSpringNearCacheService.get(cacheKey);
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
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeCalendarSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    nodeCalendarSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    NodeCalendarCacheKey cacheKey = testUtil.getNodeCalendarCacheKey();

    nodeCalendarSpringNearCacheService.deleteAll();
    CacheValue cacheValue = nodeCalendarSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void selfRegisterTest() {
    doNothing().when(nearCacheRegistry).registerNearCacheEntity(any(), any(), any());
    nodeCalendarSpringNearCacheService.selfRegister();

    verify(nearCacheRegistry, times(1)).registerNearCacheEntity(any(), any(), any());
  }

  @Test
  void getEntityName() {
    assertEquals(
        NearCacheConstants.NODE_CALENDAR_ENTITY_NAME,
        nodeCalendarSpringNearCacheService.getEntityName());
  }
}
