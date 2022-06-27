package com.hbc.node.data.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.common.response.BaseResponse;
import com.hbc.core.cache.mapper.GenericMapper;
import com.hbc.node.data.cache.domain.NodeDataCacheKey;
import com.hbc.node.data.cache.domain.NodeDataCacheValue;
import com.hbc.node.data.spring.cache.util.TestUtil;
import com.hbc.node.domain.outbound.NodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeMapperTest {

  @InjectMocks
  private GenericMapper<NodeDataCacheKey, NodeDataCacheValue, String, BaseResponse<NodeResponse>>
      genericMapper = new NodeDataMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(new NodeDataCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    NodeDataCacheValue nodeDataCacheValue = testUtil.getNodeCacheValue();
    BaseResponse<NodeResponse> response = testUtil.getNodeResponse();

    Assertions.assertEquals(nodeDataCacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    NodeDataCacheValue cacheValue = testUtil.getNodeCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
