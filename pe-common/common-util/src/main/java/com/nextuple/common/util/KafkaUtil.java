package com.nextuple.common.util;

import java.nio.charset.Charset;

public class KafkaUtil {

  private KafkaUtil() {
    // Use static methods
  }

  /**
   * Parse kafka header value to appropriate object
   *
   * @param value Kafka header value
   * @return Value object, if value object is null then it will return "null" as a string
   */
  public static Object parseHeaderValue(Object value) {
    if (value instanceof byte[]) return new String((byte[]) value, Charset.defaultCharset());

    if (value == null) return "null";

    return value;
  }
}
