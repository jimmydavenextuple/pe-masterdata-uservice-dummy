package com.hbc.common.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;

/**
 * Test class for the Json Util
 *
 * @author Sridhar Kandimalla
 */
class JsonUtilTest {

  @Description("Test to convert to a class")
  @Test
  void convertToObject() {
    final String json =
        "{\"id\":\"12345\",\"name\":\"Sridhar Kandimalla\",\"email\":\"sridhar.kandimalla@nextuple.com\"}";
    /** check if this was able to convert */
    assertNotNull(JsonUtil.convertToObject(json, HashMap.class));
  }

  @Description("Test to convert to a typereference")
  @Test
  void convertToObjectType() throws IOException {
    final String json =
        "{\"id\":\"12345\",\"name\":\"Sridhar Kandimalla\",\"email\":\"sridhar.kandimalla@nextuple.com\"}";
    /** check if this was able to convert */
    assertNotNull(JsonUtil.convertToObject(json, new TypeReference<Map<String, Object>>() {}));
  }

  @Description("Test to convert")
  @Test
  void convert() {
    final String json =
        "{\"id\":\"12345\",\"name\":\"Sridhar Kandimalla\",\"email\":\"sridhar.kandimalla@nextuple.com\"}";
    /** check the convert is not null */
    assertNotNull(JsonUtil.convert(json));
  }
}
