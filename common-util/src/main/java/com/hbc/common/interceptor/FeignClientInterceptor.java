package com.hbc.common.interceptor;

import com.hbc.common.constants.CommonConstants;
import com.hbc.common.context.CurrentThreadContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate requestTemplate) {

    log.debug("------ Inside FeignClientInterceptor ------");

    // Attach Authorization header
    if (!requestTemplate.headers().containsKey(CommonConstants.AUTHORIZATION_HEADER)) {
      requestTemplate.header(
          CommonConstants.AUTHORIZATION_HEADER,
          CurrentThreadContext.getLogContext().getAuthorizationHeader());
    }
  }
}
