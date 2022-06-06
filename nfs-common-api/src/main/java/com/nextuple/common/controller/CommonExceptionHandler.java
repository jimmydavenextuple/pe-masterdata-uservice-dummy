package com.nextuple.common.controller;

import com.nextuple.common.response.error.ErrorResponse;
import com.nextuple.common.response.error.ErrorType;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.common.util.JsonUtil;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Component
@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);

  //TODO - Extract common util into common library module - DateUtil, ErrorResponse, LogContext, KafkaContext, Filter

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    ErrorResponse.ErrorResponseBuilder builder =
        new ErrorResponse.ErrorResponseBuilder(ErrorType.ERROR, 0x000002);
    builder.exception(e);
    builder.message("Bad Request");
    e.getBindingResult().getFieldErrors().stream()
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

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
    String message = e.getMessage();
    if (!ObjectUtils.isEmpty(e.contentUTF8())) {
      Map<String, Object> responseMap = JsonUtil.convertToObject(e.contentUTF8(), Map.class);
      message = responseMap.getOrDefault("message", message).toString();
    }
    return ResponseEntity.status(e.status())
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0x000003).message(message).exception(e).build());
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder(ErrorType.ERROR, 0xfffff6).message(e.getMessage()).build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUncaughtException(Exception e) {
    logger.warn("Uncaught exception, exception : {}", e.getClass().toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ErrorResponse.builder(ErrorType.ERROR, 0xfffff7)
                .message("Internal server error")
                .build());
  }
}
