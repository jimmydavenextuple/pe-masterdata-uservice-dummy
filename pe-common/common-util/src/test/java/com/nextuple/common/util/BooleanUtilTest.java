package com.nextuple.common.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nextuple.common.exception.CommonServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BooleanUtilTest {
  @Nested
  @DisplayName("checkValidBoolean Tests")
  class CheckValidBooleanTests {
    @DisplayName("when the value is null, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsNull() {
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(null, field));

      assertEquals(
          field + ": Boolean value is empty/null", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is invalid, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsInvalid() {
      String val = "INVALID";
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(val, field));

      assertEquals(field + ": Invalid boolean value", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is empty string, it throws CommonServiceException")
    @Test
    void checkValidBooleanWhenValueIsEmpty() {
      String val = "";
      String field = "field";

      Exception exception =
          assertThrows(
              CommonServiceException.class, () -> BooleanUtil.checkValidBooleanValue(val, field));

      assertEquals(
          field + ": Boolean value is empty/null", exception.getMessage(), "Exception message");
    }

    @DisplayName("when the value is a boolean, it does not throw exception")
    @Test
    void checkValidBooleanWhenValueIsBoolean() {
      String val = "true";
      String field = "field";

      assertDoesNotThrow(() -> BooleanUtil.checkValidBooleanValue(val, field));
    }
  }
}
