package com.hbc.common.kafka.config.util;

import java.util.HashMap;
import java.util.Map;

public class CommonKafkaUtil {

  public static Map<String, Object> flattenAndReplace(Map<String, Object> producer) {
    Map<String, Object> flattenedMap = new HashMap<>();
    producer
        .entrySet()
        .forEach(
            entry -> {
              if (entry.getValue() instanceof Map) {
                Map<String, Object> childMap =
                    flattenAndReplace((Map<String, Object>) entry.getValue());
                childMap.forEach(
                    (k, v) -> {
                      flattenedMap.put(String.join(".", entry.getKey(), k), v);
                    });
              } else {
                flattenedMap.put(entry.getKey(), entry.getValue());
              }
            });
    return flattenedMap;
  }
}
