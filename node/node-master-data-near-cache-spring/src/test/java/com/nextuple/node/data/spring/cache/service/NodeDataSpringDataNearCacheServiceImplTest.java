package com.nextuple.node.data.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeDataSpringDataNearCacheServiceImplTest {

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks private NodeDataSpringDataNearCacheServiceImpl nodeDataSpringDataNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private GenericFeignCacheService<NodeDataCacheKey, NodeDataCacheValue> feignCacheService;

  @Test
  void getTestForValidData() {

    NodeDataCacheKey cacheKey = testUtil.getNodeCacheKey();
    NodeDataCacheValue cacheValue = testUtil.getNodeCacheValue();

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = nodeDataSpringDataNearCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);
    // Second Invocation
    CacheValue cacheValue2 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);
    // Third Invocation
    CacheValue cacheValue3 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getTestForInValidData() {
    NodeDataCacheKey cacheKey = testUtil.getNodeCacheKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeDataSpringDataNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeDataCacheKey cacheKey = testUtil.getNodeCacheKey();
    nodeDataSpringDataNearCacheService.delete(cacheKey);
    CacheValue cacheValue = nodeDataSpringDataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    NodeDataCacheKey cacheKey = testUtil.getNodeCacheKey();
    nodeDataSpringDataNearCacheService.deleteAll();
    CacheValue cacheValue = nodeDataSpringDataNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }
}
