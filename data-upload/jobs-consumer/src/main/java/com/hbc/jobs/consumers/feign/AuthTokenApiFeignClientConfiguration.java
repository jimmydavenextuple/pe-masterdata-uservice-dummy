package com.hbc.jobs.consumers.feign;

import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AuthTokenApiFeignClientConfiguration {

  @Value("${auth-token.username}")
  private String username;

  @Value("${auth-token.password}")
  private String password;

  @Bean
  public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor(username, password);
  }

  @Bean
  public Encoder encoder() {
    return new FormEncoder();
  }
}
