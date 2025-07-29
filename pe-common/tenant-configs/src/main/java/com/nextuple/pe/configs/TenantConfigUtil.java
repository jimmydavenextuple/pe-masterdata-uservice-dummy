package com.nextuple.pe.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nextuple.common.enums.CapacityType;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class TenantConfigUtil {
  public Map<CapacityType, Integer> parseCapacityConfigAsInteger(
      String configString, String defaultValue) {
    if (ObjectUtils.isEmpty(configString)) {
      configString = defaultValue;
    }

    return parseCapacityConfig(configString, JsonElement::getAsInt);
  }

  public Map<CapacityType, String> parseCapacityConfigAsString(String configString) {
    return parseCapacityConfig(configString, JsonElement::getAsString);
  }

  private <T> Map<CapacityType, T> parseCapacityConfig(
      String configString, Function<JsonElement, T> valueExtractor) {

    if (ObjectUtils.isEmpty(configString)) {
      return Map.of();
    }

    Map<CapacityType, T> result = new EnumMap<>(CapacityType.class);

    try {
      JsonObject jsonObject = JsonParser.parseString(configString.trim()).getAsJsonObject();

      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        try {
          CapacityType type = CapacityType.fromString(entry.getKey().trim());
          T value = valueExtractor.apply(entry.getValue());
          result.put(type, value);
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
          log.error("Invalid capacity type in config: {}", entry.getKey(), e);
          // Skip invalid capacity types
        }
      }
    } catch (Exception e) {
      log.error("Error parsing capacity config: {}", configString, e);
      // Return an empty map if parsing fails
    }

    return result;
  }
}
