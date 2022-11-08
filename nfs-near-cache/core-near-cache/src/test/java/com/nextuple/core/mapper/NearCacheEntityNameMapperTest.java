package com.nextuple.core.mapper;

import static com.nextuple.core.constants.NearCacheConstants.CARRIER_ENTITY_NAME;
import static com.nextuple.core.constants.NearCacheConstants.NODE_ENTITY_NAME;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class NearCacheEntityNameMapperTest {

  @Test
  void getEntityMapping() {
    Map<String, String> registry = NearCacheEntityNameMapper.getEntityMapping();
    assertNotNull(registry);
    assertEquals(NODE_ENTITY_NAME, registry.get("NodeEntity"));
    assertEquals(CARRIER_ENTITY_NAME, registry.get("CarrierServiceEntity"));
  }
}
