package com.nextuple.common.interceptor;

import com.nextuple.common.context.CurrentThreadContext;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

@ControllerAdvice
public class CustomRequestBodyAdvice implements RequestBodyAdvice {

  @Value("${tenantcheck:true}")
  private boolean tenantCheckEnabled;

  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @Override
  public HttpInputMessage beforeBodyRead(
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType)
      throws IOException {
    return inputMessage;
  }

  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {

    if (!Boolean.TRUE.equals(tenantCheckEnabled)) return body;

    String tenantId = CurrentThreadContext.getLogContext().getTenantId();
    if (Objects.isNull(tenantId))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "TenantId not found");

    Field field = getObjectProperty(body, "orgId");
    if (Objects.nonNull(field)) {
      field.setAccessible(true); // NOSONAR
      try {
        String orgId = (String) field.get(body);
        if (!tenantId.equals(orgId)) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
      } catch (IllegalAccessException e) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
    }
    return body;
  }

  @Override
  public Object handleEmptyBody(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return null;
  }

  private Field getObjectProperty(Object obj, String propertyName) {
    List<Field> properties = getAllFields(obj);
    for (Field field : properties) {
      if (field.getName().equalsIgnoreCase(propertyName)) {
        return field;
      }
    }
    return null;
  }

  private static List<Field> getAllFields(Object obj) {
    List<Field> fields = new ArrayList<>();
    getAllFieldsRecursive(fields, obj.getClass());
    return fields;
  }

  private static List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {
    Collections.addAll(fields, type.getDeclaredFields());

    if (type.getSuperclass() != null) {
      fields = getAllFieldsRecursive(fields, type.getSuperclass());
    }

    return fields;
  }
}
