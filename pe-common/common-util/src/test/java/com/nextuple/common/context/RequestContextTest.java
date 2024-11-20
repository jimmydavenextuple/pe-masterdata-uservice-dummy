package com.nextuple.common.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestContextTest {
  @Test
  void requestContextTest() {
    RequestContext requestContext = RequestContext.builder().build();
    requestContext.put("test-1", new HashMap<>());
    requestContext.put("test-2", new HashSet<>());

    Assertions.assertTrue(requestContext.get("test-1") instanceof HashMap);
    Assertions.assertTrue(requestContext.get("test-2") instanceof HashSet);
  }

  @Test
  void toMapTest() {
    RequestContext requestContext = RequestContext.builder().build();
    Map<String, Object> requestContextMap = requestContext.toMap();
    Assertions.assertTrue(requestContextMap.isEmpty());

    requestContext.put("test-1", new HashMap<>());
    requestContext.put("test-2", new HashSet<>());

    requestContextMap = requestContext.toMap();
    Assertions.assertEquals(2, requestContextMap.size());
  }
}
