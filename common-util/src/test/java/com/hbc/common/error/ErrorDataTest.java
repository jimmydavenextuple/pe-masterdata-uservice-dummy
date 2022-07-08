package com.hbc.common.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hbc.common.response.error.ErrorData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ErrorDataTest {

  @DisplayName("Should set everything to null with the no parameter constructor")
  @Test
  void noParameterConstructorTest() {
    String msg = "test message";
    Integer code = 201;

    ErrorData errorData = new ErrorData();
    assertNull(errorData.getCode(), "Code should be null");
    assertNull(errorData.getMessage(), "Message should be null");

    errorData.setCode(code);
    errorData.setMessage(msg);
    assertEquals(code, errorData.getCode(), "Code should not be null");
    assertEquals(msg, errorData.getMessage(), "Message should not be null");
  }

  @DisplayName("Should set the `message` and `code` to match arguments from the constructor.")
  @Test
  void allArgsConstructorTest() {
    String msg = "test message";
    Integer code = 201;

    ErrorData errorData = new ErrorData(code, msg);

    assertEquals(code, errorData.getCode(), "Code should not be null");
    assertEquals(msg, errorData.getMessage(), "Message should not be null");
  }

  @DisplayName("Should get code and msg as provided.")
  @Test
  void builderAndDataTest() {
    String msg = "test message";
    Integer code = 201;

    ErrorData errorData = ErrorData.builder().code(code).message(msg).build();

    assertEquals(code, errorData.getCode(), "Code should not be null");
    assertEquals(msg, errorData.getMessage(), "Message should not be null");
  }
}
