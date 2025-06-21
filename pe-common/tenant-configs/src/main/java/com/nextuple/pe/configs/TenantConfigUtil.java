package com.nextuple.pe.configs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nextuple.common.enums.CapacityType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.util.ObjectUtils;

public class TenantConfigUtil {
  private static final Gson gson = new Gson();

  private TenantConfigUtil() {}

  public static Map<CapacityType, Integer> parseCapacityConfigAsInteger(
      String configString, String defaultValue) {
    if (ObjectUtils.isEmpty(configString)) {
      configString = defaultValue;
    }

    return parseCapacityConfig(configString, JsonElement::getAsInt);
  }

  public static Map<CapacityType, String> parseCapacityConfigAsString(
      String configString, String defaultValue) {
    return parseCapacityConfig(configString, JsonElement::getAsString);
  }

  private static <T> Map<CapacityType, T> parseCapacityConfig(
      String configString, Function<JsonElement, T> valueExtractor) {

    if (ObjectUtils.isEmpty(configString)) {
      return Map.of();
    }

    Map<CapacityType, T> result = new HashMap<>();

    try {
      JsonObject jsonObject = JsonParser.parseString(configString.trim()).getAsJsonObject();

      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        try {
          CapacityType type = CapacityType.fromString(entry.getKey().trim());
          T value = valueExtractor.apply(entry.getValue());
          result.put(type, value);
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
          // Skip invalid keys or parsing issues
        }
      }
    } catch (Exception e) {
      // Malformed JSON — return empty map
    }

    return result;
  }
}
