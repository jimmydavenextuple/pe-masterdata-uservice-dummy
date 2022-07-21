package com.hbc.core.spring.config;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.core.spring.util.TestUtil;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CachePropertiesTest {

  @InjectMocks private CacheProperties cacheProperties;
  @InjectMocks private TestUtil testUtil;

  @Test
  void setCacheDefaults() {
    Map<String, String> cacheMap = cacheProperties.setCacheDefaults();
    assertEquals(testUtil.getCacheMap().size(), cacheMap.size());
  }
}
