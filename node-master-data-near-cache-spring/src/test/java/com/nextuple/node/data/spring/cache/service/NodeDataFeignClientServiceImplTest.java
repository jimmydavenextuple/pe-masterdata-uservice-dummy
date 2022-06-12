package com.nextuple.node.data.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.domain.node.NodeResponse;
import com.nextuple.node.data.cache.domain.NodeDataCacheKey;
import com.nextuple.node.data.cache.domain.NodeDataCacheValue;
import com.nextuple.node.data.spring.cache.feign.NodeDataFeignImpl;
import com.nextuple.node.data.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeDataFeignClientServiceImplTest {

  @InjectMocks private NodeDataFeignClientServiceImpl nodeDataFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>>
      mapper;

  @Mock private NodeDataFeignImpl nodeDataFeign;

  @Test
  void get() {

    NodeDataCacheKey cacheKey = testUtil.getNodeCacheKey();
    NodeDataCacheValue cacheValue = testUtil.getNodeCacheValue();
    BaseResponse<NodeResponse> response = testUtil.getNodeResponse();

    Mockito.when(nodeDataFeign.getNodeDetails(cacheKey.getNodeId(), cacheKey.getOrgId()))
        .thenReturn(response);
    Mockito.when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, nodeDataFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    NodeDataCacheKey nodeDataCacheKey = new NodeDataCacheKey();

    Mockito.when(mapper.responseToCacheValue(any()))
        .thenThrow(new RuntimeException("Error message"));
    assertNull(nodeDataFeignClientService.get(nodeDataCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
