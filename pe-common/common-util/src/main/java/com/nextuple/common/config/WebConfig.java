package com.nextuple.common.config;

import com.nextuple.common.interceptor.RestRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${tenantcheck:true}")
  private boolean tenantCheckEnabled;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new RestRequestInterceptor(tenantCheckEnabled))
        .excludePathPatterns("/v3/**")
        .excludePathPatterns("/actuator/**");
  }
}
