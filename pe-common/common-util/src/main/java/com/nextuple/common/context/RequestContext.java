package com.nextuple.common.context;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public class RequestContext {
  private Map<String, Object> customRequestFields;

  public RequestContext put(String key, Object value) {
    if (customRequestFields == null) {
      customRequestFields = new HashMap<>();
    }
    customRequestFields.put(key, value);
    return this;
  }

  public Object get(String key) {
    return get(key, null);
  }

  public Object get(String key, Object defaultIfNotFound) {
    if (customRequestFields == null) {
      customRequestFields = new HashMap<>();
    }
    return customRequestFields.getOrDefault(key, defaultIfNotFound);
  }

  public Map<String, Object> toMap() {
    if (customRequestFields == null) {
      customRequestFields = new HashMap<>();
    }
    Map<String, Object> result = new HashMap<>();
    result.putAll(customRequestFields);
    return result;
  }
}
