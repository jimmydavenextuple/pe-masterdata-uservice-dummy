package com.nextuple.common.interceptor;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.nextuple.common.context.CurrentThreadContext;
import com.nextuple.common.util.ExampleObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class CustomRequestBodyAdviceTest {
  @InjectMocks CustomRequestBodyAdvice customRequestBodyAdvice;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    customRequestBodyAdvice = new CustomRequestBodyAdvice();
  }

  @Test
  void afterBodyReadValidationSuccess() {
    ReflectionTestUtils.setField(customRequestBodyAdvice, "tenantCheckEnabled", true);
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE");
    ExampleObject object = new ExampleObject();
    object.setOrderNo("ORD1");
    object.setOrderType("BOPIS");
    object.setOrgId("NEXTUPLE");
    object.setQuantity(10);
    ExampleObject result =
        (ExampleObject) customRequestBodyAdvice.afterBodyRead(object, null, null, null, null);
    Assertions.assertEquals("ORD1", result.getOrderNo());
    Assertions.assertEquals("BOPIS", result.getOrderType());
    Assertions.assertEquals(10, result.getQuantity());
  }

  @Test
  void afterBodyReadValidationFailure() {
    ReflectionTestUtils.setField(customRequestBodyAdvice, "tenantCheckEnabled", true);
    CurrentThreadContext.getLogContext().setTenantId("NEXTUPLE_GR");
    ExampleObject object = new ExampleObject();
    object.setOrderNo("ORD1");
    object.setOrderType("BOPIS");
    object.setOrgId("NEXTUPLE");
    object.setQuantity(10);
    ResponseStatusException e =
        Assertions.assertThrows(
            ResponseStatusException.class,
            () -> {
              customRequestBodyAdvice.afterBodyRead(object, null, null, null, null);
            });
    Assertions.assertEquals(FORBIDDEN, e.getStatusCode());
  }
}
