package com.nextuple.node.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.controltower.common.base.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.core.node.outbound.NodeValidationResponse;
import com.nextuple.nodde.spring.cache.mapper.NodeMapper;
import com.nextuple.node.cache.domain.NodeCacheKey;
import com.nextuple.node.cache.domain.NodeCacheValue;
import com.nextuple.node.cache.domain.NodeValidationRequest;
import com.nextuple.node.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NodeMapperTest {

  @InjectMocks
  private GenericMapper<
          NodeCacheKey, NodeCacheValue, NodeValidationRequest, BaseResponse<NodeValidationResponse>>
      genericMapper = new NodeMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey(null));
  }

  @Test
  void cacheKeyToRequest() {
    NodeCacheKey cacheKey = testUtil.getNodeCacheKey("node_123", "Nextuple");
    assertEquals(
        cacheKey.getNodeTenantDetails().getNodeNo(),
        genericMapper.cacheKeyToRequest(cacheKey).getNodes().get(0).getNodeNo());
  }

      @Test
      void responseToCacheValue() {
          NodeCacheValue cacheValue = testUtil.getNodeCacheValue("123", "node_1");
          BaseResponse<NodeValidationResponse> baseResponse = testUtil.getBaseResponseOfNodeValidationResponse("123","node_1");
          assertEquals(cacheValue.getNodeDetails().getNodeName(),genericMapper.responseToCacheValue(baseResponse).getNodeDetails().getNodes().get(0).getNodeName());
        assertEquals(cacheValue.getNodeDetails().getNodeNo(),genericMapper.responseToCacheValue(baseResponse).getNodeDetails().getNodes().get(0).getNodeNo());
  }

  @Test
  void cacheValueToResponse() {
    NodeCacheValue cacheValue = testUtil.getNodeCacheValue("123", "node_1");
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
