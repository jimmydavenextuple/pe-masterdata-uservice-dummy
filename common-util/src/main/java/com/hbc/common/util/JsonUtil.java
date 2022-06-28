package com.hbc.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the utility to deserialize and serialize the JSON to Object and vice versa
 *
 * @author Sridhar Kandimalla
 */
public class JsonUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

  static {
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  private JsonUtil() {
    // Use static methods
  }

  /**
   * This is the method to deserialize JSON content from given JSON content String.
   *
   * @param jsonString
   * @param classType
   * @param <T>
   * @return
   */
  public static <T> T convertToObject(String jsonString, Class<T> classType) {
    T obj = null;
    try {
      obj = objectMapper.readValue(jsonString, classType);
    } catch (Exception e) {
      logger.error("Unable to convert json string to required class type", e);
    }
    return obj;
  }

  /**
   * Method to deserialize JSON content from given JSON content String.
   *
   * @param jsonString
   * @param typeReference
   * @param <T>
   * @return
   * @throws IOException
   */
  public static <T> T convertToObject(String jsonString, TypeReference<T> typeReference)
      throws IOException {
    return objectMapper.readValue(jsonString, typeReference);
  }

  /**
   * Method that can be used to serialize any Java value as a String
   *
   * @param object
   * @return
   */
  public static String convert(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      logger.error("Unable to convert object to json string", e);
      return null;
    }
  }
}
