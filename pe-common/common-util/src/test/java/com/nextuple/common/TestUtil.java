package com.nextuple.common;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import java.util.HashMap;
import java.util.Map;

public class TestUtil {

  public static Throwable getConnectionRefusedFeignException(Exception cause) {
    Request request =
        Request.create(Request.HttpMethod.GET, "url", new HashMap<>(), null, new RequestTemplate());
    return new FeignException.ServiceUnavailable("methodKey", request, null, Map.of())
        .initCause(cause);
  }
}
