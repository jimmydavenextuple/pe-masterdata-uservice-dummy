package com.hbc.nodecarrier.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.common.response.BaseResponse;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.hbc.nodecarrier.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierMapperTest {

  @InjectMocks private NodeCarrierMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getNodeCarrierCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    NodeCarrierCacheValue nodeCarrierCacheValue = testUtil.getNodeCarrierCacheValue();

    BaseResponse<NodeCarrierResponse> response = testUtil.getBaseResponseOfNodeCarrierResponse();

    assertEquals(nodeCarrierCacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getNodeCarrierCacheValue()));
  }
}
