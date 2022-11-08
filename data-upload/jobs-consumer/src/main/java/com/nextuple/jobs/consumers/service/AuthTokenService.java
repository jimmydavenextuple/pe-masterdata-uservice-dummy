package com.nextuple.jobs.consumers.service;

import com.nextuple.jobs.consumers.feign.AuthTokenAPI;
import com.nextuple.jobs.consumers.feign.AuthTokenRequest;
import com.nextuple.jobs.consumers.feign.AuthTokenResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class AuthTokenService {

  @Value("${auth-token.grant-type:client_credentials}")
  public String grantType;

  @Value("${auth-token.scope:sfcc-resources/edd}")
  public String scope;

  private final AuthTokenAPI authTokenAPI;

  public AuthTokenService(AuthTokenAPI authTokenAPI) {
    this.authTokenAPI = authTokenAPI;
  }

  public String getAuthToken(String authHeader, String authExpiryTs) {
    log.debug("Auth Header is {} and expiry is {}", authHeader, authExpiryTs);
    if (!hasAuthExpired(authExpiryTs)) {
      return authHeader;
    }
    AuthTokenResponse response = generateAuthToken();
    String authToken = response.getAccessToken();
    log.debug("Auth Token generated is : {}", authToken);
    return "Bearer " + authToken;
  }

  private boolean hasAuthExpired(String authExpiryTs) {
    if (!StringUtils.hasLength(authExpiryTs)) {
      return true;
    }
    var dateTime = LocalDateTime.now();
    var expiryTime = LocalDateTime.parse(authExpiryTs, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    log.debug("Current Time is : {} and expiry time is {}", dateTime, expiryTime);
    return dateTime.isAfter(expiryTime);
  }

  public AuthTokenResponse generateAuthToken() {
    var authTokenRequest = AuthTokenRequest.builder().grant_type(grantType).scope(scope).build();
    return authTokenAPI.getAuthToken(authTokenRequest);
  }
}
