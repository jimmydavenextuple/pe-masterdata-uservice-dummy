package com.hbc.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.hbc.common.context.CurrentThreadContext;
import com.hbc.common.response.error.ErrorResponse;
import com.hbc.common.response.error.ErrorType;
import com.hbc.common.response.error.FieldError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class CommonExceptionHandler {
  private static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
  private static org.slf4j.Logger slf4jLogger =
      org.slf4j.LoggerFactory.getLogger(CommonExceptionHandler.class);
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
    logError(exception, exception.getMessage(), EMPTY_MAP);
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

  @ExceptionHandler(CommonServiceException.class)
  public ResponseEntity<ErrorResponse> handleCommonServiceException(CommonServiceException e) {
    logError(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.status(e.getHttpStatus())
        .body(
            ErrorResponse.builder(ErrorType.ERROR, e.getErrorCode())
                .message(e.getMessage())
                .errorField(e.getFieldInfo())
                .build());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorResponse> handleIOException(IOException e) {
    logError(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x2772)
                .message("File not found!")
                .errorField("fileUri", FieldError.builder().rejectedValue(e.getMessage()).build())
                .build());
  }

  @ExceptionHandler(PromiseEngineException.class)
  public ResponseEntity<ErrorResponse> handlePromiseEngineException(PromiseEngineException e) {
    return ResponseEntity.badRequest()
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000001)
                .message(e.getMessage() + "[" + e.getExceptionCode().getErrorCode() + "]")
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
    MDC.put("exception_class", e.getClass().getName());
    logError(e, e.getMessage(), EMPTY_MAP);
    return ResponseEntity.internalServerError()
        .body(ErrorResponse.builder(ErrorType.ERROR, 0x000001).message(e.getMessage()).build());
  }

  public void logError(
      Throwable t, String message, Map<String, String> metadata, Object... params) {
    logStatement(
        () -> {
          if (t != null) {
            slf4jLogger.error("Exception thrown : ", t);
          }
          slf4jLogger.error(message, params);
        },
        metadata);
  }

  private void logStatement(Runnable statement, Map<String, String> metadata) {
    populateLogContext();
    attachMetadata(metadata);
    statement.run();
    clearMetadata(metadata);
  }

  private void populateLogContext() {
    // Populate MDC with fields from LogContext
    CurrentThreadContext.getLogContext().toMap().forEach(MDC::put);
  }

  private void attachMetadata(Map<String, String> metadata) {
    if (!CollectionUtils.isEmpty(metadata)) {
      metadata.forEach((key, value) -> MDC.put("metadata_" + key, value));
    }
  }

  private void clearMetadata(Map<String, String> metadata) {
    if (!CollectionUtils.isEmpty(metadata)) {
      metadata.forEach((key, value) -> MDC.remove("metadata_" + key));
    }
  }
}
