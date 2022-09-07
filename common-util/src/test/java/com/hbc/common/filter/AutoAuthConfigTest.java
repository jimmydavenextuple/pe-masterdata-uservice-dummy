package com.hbc.common.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AutoAuthConfigTest {

  @InjectMocks AutoAuthConfig autoAuthConfig;

  @Test
  void authPropertiesTest() {
    ReflectionTestUtils.setField(autoAuthConfig, "filterEnabled", true);
    ReflectionTestUtils.setField(
        autoAuthConfig, "claims", Map.of("scope", List.of("scope1", "scope2")));
    ReflectionTestUtils.setField(autoAuthConfig, "issuer", "issuer1");

    AuthProperties authProperties = autoAuthConfig.authProperties();

    assertNotNull(authProperties);
    assertTrue(authProperties.isFilterEnabled());
    assertEquals("issuer1", authProperties.getIssuer());
  }
}
