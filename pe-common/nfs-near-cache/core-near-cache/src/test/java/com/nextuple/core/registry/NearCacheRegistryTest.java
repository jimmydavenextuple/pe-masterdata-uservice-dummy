package com.nextuple.core.registry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NearCacheRegistryTest {

  @InjectMocks NearCacheRegistry nearCacheRegistry;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(nearCacheRegistry, "registry", new HashMap<>());
  }

  @Test
  void getRegistry() {
    nearCacheRegistry.registerNearCacheEntity("entity name", "class name", "drop type");
    Map<String, String> registry = nearCacheRegistry.getRegistry("entity name");
    assertNotNull(registry);
    assertEquals("drop type", registry.get("class name"));
  }
}
