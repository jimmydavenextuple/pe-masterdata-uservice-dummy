package com.hbc.jobs.consumers.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "${auth-token.name}",
    url = "${auth-token.url}",
    configuration = AuthTokenApiFeignClientConfiguration.class)
public interface AuthTokenAPI {

  @PostMapping(
      value = "/oauth2/token",
      consumes = "application/x-www-form-urlencoded",
      produces = "application/json")
  AuthTokenResponse getAuthToken(@RequestBody AuthTokenRequest request);
}
