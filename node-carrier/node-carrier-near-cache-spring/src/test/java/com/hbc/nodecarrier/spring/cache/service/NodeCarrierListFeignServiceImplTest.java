package com.hbc.nodecarrier.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheKey;
import com.hbc.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.hbc.nodecarrier.spring.cache.feign.NodeCarrierListFeignImpl;
import com.hbc.nodecarrier.spring.cache.mapper.NodeCarrierListMapper;
import com.hbc.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierListFeignServiceImplTest {

  @InjectMocks private NodeCarrierListFeignServiceImpl nodeCarrierListFeignService;

  @InjectMocks private TestUtil testUtil;

  @Mock private NodeCarrierListMapper mapper;

  @Mock private NodeCarrierListFeignImpl nodeCarrierFeign;

  @Test
  void getTest() {
    NodeCarrierListCacheKey cacheKey = testUtil.getNodeCarrierListCacheKey();
    NodeCarrierListCacheValue cacheValue = testUtil.getNodeCarrierListCacheValue();

    when(mapper.responseToCacheValue(any())).thenReturn(cacheValue);
    when(nodeCarrierFeign.getNodeCarrierList(any(), any(), any()))
        .thenReturn(testUtil.getListOfBaseResponseOfNodeCarrierResponse());

    assertEquals(cacheValue, nodeCarrierListFeignService.get(cacheKey));
    assertFalse(nodeCarrierListFeignService.get(cacheKey).isUndefined());
    verify(mapper, times(2)).responseToCacheValue(any());
  }

  @Test
  void getExceptionTest() {
    NodeCarrierListCacheKey invalidCacheKey = testUtil.getNodeCarrierListCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(nodeCarrierListFeignService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
