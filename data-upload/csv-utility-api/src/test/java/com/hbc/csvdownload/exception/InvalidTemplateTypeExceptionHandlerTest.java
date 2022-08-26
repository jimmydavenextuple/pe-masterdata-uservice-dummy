package com.hbc.csvdownload.exception;

import com.hbc.common.response.error.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class InvalidTemplateTypeExceptionHandlerTest {

  @InjectMocks InvalidTemplateTypeExceptionHandler invalidTemplateTypeExceptionHandler;

  @Test
  void handleInvalidTemplateTypeException() {
    InvalidTemplateTypeException invalidTemplateTypeException =
        new InvalidTemplateTypeException("The template type is invalid", "invalidTemplateType");
    ResponseEntity<ErrorResponse> errorResponse =
        invalidTemplateTypeExceptionHandler.handleInvalidTemplateTypeException(
            invalidTemplateTypeException);
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
    Assertions.assertNotNull(errorResponse.getBody());
  }
}
