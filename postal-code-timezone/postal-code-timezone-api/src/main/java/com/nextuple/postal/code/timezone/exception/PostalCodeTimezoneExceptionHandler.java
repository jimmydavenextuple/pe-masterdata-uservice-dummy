package com.nextuple.postal.code.timezone.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class PostalCodeTimezoneExceptionHandler {
  private static final Pattern ENUM_MSG =
      Pattern.compile("values accepted for Enum class: \\[[^\\]]*\\]", Pattern.MULTILINE);
  private static final Pattern DATE_MSG =
      Pattern.compile(
          "not compatible with any of standard forms \\(\\\"yyyy-MM-dd'T'HH:mm:ss\\.SSSX\\\", "
              + "\\\"yyyy-MM-dd'T'HH:mm:ss\\.SSS\\\", "
              + "\\\"EEE, dd MMM yyyy HH:mm:ss zzz\\\", "
              + "\\\"yyyy-MM-dd\\\"\\)",
          Pattern.MULTILINE);
  private static final Pattern EMPTY_MSG =
      Pattern.compile("Cannot coerce empty String", Pattern.MULTILINE);

  private static final List<Pattern> PATTERNS_TO_MATCH =
      new ArrayList<>(Arrays.asList(ENUM_MSG, DATE_MSG, EMPTY_MSG));

  private static final String BAD_REQUEST = "Bad Request";

  @ExceptionHandler(PromiseEngineException.class)
  public ResponseEntity<ErrorResponse> handlePromiseEngineException(PromiseEngineException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000001)
                .message(e.getMessage() + "[" + e.getExceptionCode().getErrorCode() + "]")
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse.ErrorResponseBuilder builder =
        new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0x000002);
    builder.message(BAD_REQUEST);
    e.getBindingResult()
        .getFieldErrors()
        .forEach(
            x ->
                builder.errorField(
                    x.getField(),
                    FieldError.builder()
                        .errorMessage(x.getDefaultMessage())
                        .rejectedValue(x.getRejectedValue() + "")
                        .build()));
    return ResponseEntity.badRequest().body(builder.build());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleJsonErrors(HttpMessageNotReadableException exception) {
    Throwable cause = exception.getCause();
    if (cause instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatCause = (InvalidFormatException) cause;
      String message = invalidFormatCause.getMessage();
      for (Pattern pattern : PATTERNS_TO_MATCH) {
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
          message = matcher.group(0);
          break;
        }
      }
      return ResponseEntity.badRequest()
          .body(
              ErrorResponse.builder(ErrorType.ERROR, 0x000003)
                  .message(BAD_REQUEST)
                  .errorField(
                      invalidFormatCause.getPath().get(0).getFieldName(),
                      FieldError.builder()
                          .rejectedValue(invalidFormatCause.getValue())
                          .errorMessage(message)
                          .build())
                  .build());
    }
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000003)
                .message(exception.getMessage())
                .build());
  }
}
