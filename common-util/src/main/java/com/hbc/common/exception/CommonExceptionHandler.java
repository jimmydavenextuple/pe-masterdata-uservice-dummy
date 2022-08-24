package com.hbc.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CommonExceptionHandler {
  private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
  private static Logger slf4jLogger = LoggerFactory.getLogger(CommonExceptionHandler.class);
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    var builder = new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0x000002);
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
    slf4jLogger.error(exception, exception.getMessage(), EMPTY_MAP);
    Throwable cause = exception.getCause();
    if (cause instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatCause = (InvalidFormatException) cause;
      String message = invalidFormatCause.getMessage();
      for (Pattern pattern : PATTERNS_TO_MATCH) {
        var matcher = pattern.matcher(message);
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

  @ExceptionHandler(CommonServiceException.class)
  public ResponseEntity<ErrorResponse> handleCommonServiceException(CommonServiceException e) {
    slf4jLogger.error(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.status(e.getHttpStatus())
        .body(
            ErrorResponse.builder(ErrorType.ERROR, e.getErrorCode())
                .message(e.getMessage())
                .errorField(e.getFieldInfo())
                .build());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
    slf4jLogger.error(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x2772)
                .message("File not found!")
                .errorField("fileUri", FieldError.builder().rejectedValue(e.getMessage()).build())
                .build());
  }

  @ExceptionHandler(PromiseEngineException.class)
  public ResponseEntity<ErrorResponse> handlePromiseEngineException(PromiseEngineException e) {
    slf4jLogger.error(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000001)
                .message(e.getMessage() + "[" + e.getExceptionCode().getErrorCode() + "]")
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
    slf4jLogger.error(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.internalServerError()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0x000001).message(e.getMessage()).build());
  }
}
