package com.nextuple.jobs.framework.common.utils;

import static org.mockito.Mockito.when;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilsTest {

  @Mock FeignException feignException;

  @DisplayName("Should return Error Response that has message 'upstream error'")
  @Test
  void parseNullFeignExceptionTest() {
    ErrorResponse actual = ExceptionUtils.parseFeignException(null);
    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error")
            .build();
    Assertions.assertEquals(actual, expected, "Error Response");
  }

  @DisplayName("Should return Error Response where jsonUtil converts the UFT8 Content to be null ")
  @Test
  void parseNullUTF8FeignExceptionTest() {

    String jsonStr = "fakeStr";

    when(feignException.contentUTF8()).thenReturn(jsonStr);

    ErrorResponse actual = ExceptionUtils.parseFeignException(feignException);

    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error : " + jsonStr)
            .build();
    Assertions.assertEquals(expected, actual);
  }

  @DisplayName(
      "Should return Error Response where jsonUtil converts the UFT8 Content correctly and gets the correct exception message ")
  @Test
  void parseUTF8FeignExceptionTest() {
    String message = "message";

    when(feignException.contentUTF8()).thenReturn("");
    when(feignException.getMessage()).thenReturn(message);

    ErrorResponse actual = ExceptionUtils.parseFeignException(feignException);
    ErrorResponse expected =
        ErrorResponse.builder(ErrorType.ERROR, ExceptionUtils.UPSTREAM_ERROR_CODE)
            .message("Upstream error : " + feignException.getMessage())
            .build();

    Assertions.assertEquals(expected, actual);
  }
}
