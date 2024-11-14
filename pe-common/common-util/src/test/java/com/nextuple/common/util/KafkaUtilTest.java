package com.nextuple.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KafkaUtilTest {
  @DisplayName("Should return 'null' since headerValue is not present")
  @Test
  void parseHeaderValueNullTest() {
    Object expected = "null";
    Object actual = KafkaUtil.parseHeaderValue(null);
    assertEquals(expected, actual, "header value");
  }

  @DisplayName(
      "Should return the string value of the byte array that was the argument of the parseHeaderValue")
  @Test
  void parseHeaderValueTest() {
    String expected = "NEXTUPLE_GR";
    byte[] inputArray = expected.getBytes();
    String actual = (String) KafkaUtil.parseHeaderValue(inputArray);
    assertEquals(expected, actual, "Header Value");
  }

  @DisplayName(
      "Should not return wrong string value of the byte array that was the argument of the parseHeaderValue")
  @Test
  void parseHeaderValueTestWrongActual() {
    String expected = "NEXTUPLE_GR";
    String actual = (String) KafkaUtil.parseHeaderValue("NEXTUPLE_R".getBytes());
    assertNotEquals(expected, actual, "Header Values");
  }
}
