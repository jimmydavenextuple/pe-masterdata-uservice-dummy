package com.nextuple.common.userexit;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserExitUtil {

  @Value("${userexit.timeout.connect:10}")
  int connectTimeout;

  private HttpClient client;

  public HttpClient getHttpClient() {
    if (Objects.isNull(client)) {
      client =
          HttpClient.newBuilder()
              .connectTimeout(Duration.of(connectTimeout, ChronoUnit.SECONDS))
              .build();
    }
    return client;
  }
}
