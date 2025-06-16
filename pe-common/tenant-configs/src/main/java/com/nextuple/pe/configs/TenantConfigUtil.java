package com.nextuple.pe.configs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nextuple.common.enums.CapacityType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.ObjectUtils;

public class TenantConfigUtil {
  private static final Gson gson = new Gson();

  private TenantConfigUtil() {}

  public static Map<CapacityType, Integer> parseCapacityConfigString(
      String configString, String defaultValue) {
    if (ObjectUtils.isEmpty(configString) && ObjectUtils.isEmpty(defaultValue)) {
      return Map.of();
    } else if (ObjectUtils.isEmpty(configString) && !ObjectUtils.isEmpty(defaultValue)) {
      configString = defaultValue;
    }

    Map<CapacityType, Integer> result = new HashMap<>();

    try {
      JsonObject jsonObject = JsonParser.parseString(configString.trim()).getAsJsonObject();

      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        try {
          CapacityType type = CapacityType.fromString(entry.getKey().trim());
          Integer days = entry.getValue().getAsInt();
          result.put(type, days);
        } catch (IllegalArgumentException e) {
          // Skip invalid entries
        }
      }
    } catch (Exception e) {
      // If JSON parsing fails, return empty result
    }

    return result;
  }
}
