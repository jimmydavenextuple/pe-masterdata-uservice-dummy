package com.nextuple.node.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.nodde.spring.cache.service.NodeFeignClientServiceImpl;
import com.nextuple.nodde.spring.cache.service.NodeSpringNearCacheServiceImpl;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.spring.cache.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NodeSpringNearCacheServiceImplTest {
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks private NodeSpringNearCacheServiceImpl nodeSpringNearCacheService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeFeignClientServiceImpl nodeFeignClientService;

  @Mock private GenericFeignCacheService<NodeCacheKey, NodeCacheValue> feignCacheService;

  @Test
  void getTestForValidNodeNoAndTenantId() {

    NodeCacheKey cacheKey = testUtil.getNodeCacheKey("AM_14021", "NEXTUPLE_GR");
    NodeCacheValue cacheValue = testUtil.getNodeCacheValue("NEXTUPLE_GR", "Nextuple");

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);
    // First Invocation
    CacheValue cacheValue1 = nodeSpringNearCacheService.get(cacheKey);
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
  void getTestForInValidNodeNoAndTenantId() {
    NodeCacheKey cacheKey = testUtil.getNodeCacheKey("invalid", "invalid");
    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(nodeSpringNearCacheService.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    NodeCacheKey cacheKey = testUtil.getNodeCacheKey("AM_14021", "NEXTUPLE_GR");
    nodeSpringNearCacheService.delete(cacheKey);
    CacheValue cacheValue = nodeSpringNearCacheService.get(cacheKey);
    assertNull(cacheValue);
  }
}
