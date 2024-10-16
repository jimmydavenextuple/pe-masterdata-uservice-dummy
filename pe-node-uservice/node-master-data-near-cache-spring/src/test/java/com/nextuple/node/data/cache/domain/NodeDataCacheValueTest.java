package com.nextuple.node.data.cache.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NodeDataCacheValueTest {
  @Test
  void testUpdateContextMap() {
    NodeDataCacheValue nodeDataCacheValue =
        NodeDataCacheValue.builder()
            .nodeId("Node1")
            .zipCode("ABC123")
            .nodeType("FC")
            .nodeLabourTier("Tier1")
            .build();
    Map<String, Object> contextMap = new HashMap<>();
    nodeDataCacheValue.updateContextMap("", contextMap);
    Assertions.assertFalse(contextMap.isEmpty());
    Assertions.assertEquals("Node1", contextMap.get("/nodeId"));
  }

  @Test
  void isUndefinedTest() {
    NodeDataCacheValue nodeDataCacheValue =
        NodeDataCacheValue.builder()
            .nodeId("Node1")
            .zipCode("ABC123")
            .nodeType("FC")
            .nodeLabourTier("Tier1")
            .build();
    Assertions.assertFalse(nodeDataCacheValue.isUndefined());
  }
}
