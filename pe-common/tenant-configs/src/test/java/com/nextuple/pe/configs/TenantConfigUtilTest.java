package com.nextuple.pe.configs;

import static org.junit.jupiter.api.Assertions.*;

import com.nextuple.common.enums.CapacityType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantConfigUtilTest {

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with valid JSON config string")
  void parseCapacityConfigStringWithValidConfig() {
    String configString = "{\"outbound\": 20, \"transport\": 20, \"receiving\": 20}";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(20, result.get(CapacityType.OUTBOUND));
    assertEquals(20, result.get(CapacityType.TRANSPORT));
    assertEquals(20, result.get(CapacityType.RECEIVING));
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with null config and null default")
  void parseCapacityConfigStringWithNullConfigAndNullDefault() {
    String configString = null;
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with empty config and null default")
  void parseCapacityConfigStringWithEmptyConfigAndNullDefault() {
    String configString = "";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName(
      "Test parseCapacityConfigAsInteger with null config and valid default - specifically testing the condition")
  void parseCapacityConfigStringWithNullConfigAndValidDefault() {
    String configString = null;
    String defaultValue = "{\"outbound\": 30, \"transport\": 15, \"receiving\": 10}";

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(30, result.get(CapacityType.OUTBOUND));
    assertEquals(15, result.get(CapacityType.TRANSPORT));
    assertEquals(10, result.get(CapacityType.RECEIVING));
  }

  @Test
  @DisplayName(
      "Test parseCapacityConfigAsInteger with empty string config and valid default - specifically testing the condition")
  void parseCapacityConfigStringWithEmptyStringConfigAndValidDefault() {
    String configString = "";
    String defaultValue = "{\"outbound\": 30, \"transport\": 15, \"receiving\": 10}";

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(30, result.get(CapacityType.OUTBOUND));
    assertEquals(15, result.get(CapacityType.TRANSPORT));
    assertEquals(10, result.get(CapacityType.RECEIVING));
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with blank string config and valid default")
  void parseCapacityConfigStringWithBlankStringConfigAndValidDefault() {
    String configString = "  ";
    String defaultValue = "{\"outbound\": 30, \"transport\": 15, \"receiving\": 10}";

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with invalid JSON config")
  void parseCapacityConfigStringWithInvalidJsonConfig() {
    String configString = "invalid-json";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with valid JSON but invalid capacity type")
  void parseCapacityConfigStringWithInvalidCapacityType() {
    String configString = "{\"invalid_type\": 20, \"outbound\": 20}";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(20, result.get(CapacityType.OUTBOUND));
    assertNull(result.get("invalid_type"));
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with valid JSON but invalid value type")
  void parseCapacityConfigAsIntegerWithInvalidValueType() {
    String configString = "{\"outbound\": \"not-a-number\", \"transport\": 20}";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNull(result.get(CapacityType.OUTBOUND));
    assertEquals(20, result.get(CapacityType.TRANSPORT));
  }

  @Test
  @DisplayName("Test parseCapacityConfigAsInteger with mixed case capacity types")
  void parseCapacityConfigAsIntegerWithMixedCaseTypes() {
    String configString = "{\"OutBound\": 20, \"TRANSPORT\": 15, \"Receiving\": 10}";
    String defaultValue = null;

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(20, result.get(CapacityType.OUTBOUND));
    assertEquals(15, result.get(CapacityType.TRANSPORT));
    assertEquals(10, result.get(CapacityType.RECEIVING));
  }

  @Test
  @DisplayName(
      "Test parseCapacityConfigAsInteger with invalid config but valid default as fallback")
  void parseCapacityConfigStringWithInvalidConfigAndValidDefault() {
    String configString = "not-valid-json";
    String defaultValue = "{\"outbound\": 30, \"transport\": 15, \"receiving\": 10}";

    Map<CapacityType, Integer> result =
        TenantConfigUtil.parseCapacityConfigAsInteger(configString, defaultValue);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
