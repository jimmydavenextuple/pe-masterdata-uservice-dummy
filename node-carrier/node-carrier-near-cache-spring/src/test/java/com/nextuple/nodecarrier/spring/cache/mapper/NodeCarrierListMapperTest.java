package com.nextuple.nodecarrier.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.nodecarrier.cache.domain.NodeCarrierListCacheValue;
import com.nextuple.nodecarrier.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NodeCarrierListMapperTest {

  @InjectMocks private NodeCarrierListMapper mapper;

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(mapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(mapper.cacheKeyToRequest(testUtil.getNodeCarrierListCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    NodeCarrierListCacheValue nodeCarrierListCacheValue = testUtil.getNodeCarrierListCacheValue();

    BaseResponse<List<NodeCarrierResponse>> response =
        testUtil.getListOfBaseResponseOfNodeCarrierResponse();

    assertEquals(nodeCarrierListCacheValue, mapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    assertNull(mapper.cacheValueToResponse(testUtil.getNodeCarrierListCacheValue()));
  }
}
