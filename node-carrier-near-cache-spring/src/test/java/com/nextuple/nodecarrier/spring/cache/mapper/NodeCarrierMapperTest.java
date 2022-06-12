package com.nextuple.nodecarrier.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.domain.node.NodeCarrierResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierCacheValue;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
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
