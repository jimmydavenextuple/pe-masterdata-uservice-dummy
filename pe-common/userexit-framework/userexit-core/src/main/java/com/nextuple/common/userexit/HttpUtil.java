package com.nextuple.common.userexit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.userexit.domain.UserExitData;
import com.nextuple.common.userexit.domain.dto.UserExitConfigDataDto;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HttpUtil<T, G> {
  @Autowired UserExitUtil userExitUtil;
  @Autowired ObjectMapper objectMapper;

  @Value("${userexit.timeout.read:10}")
  int readTimeout;

  @Autowired MeterRegistry meterRegistry;

  public G makePOSTCall(
      UserExitData userExitData,
      T inputData,
      Map<String, Object> customAttributeMap,
      TypeReference<G> classType)
      throws URISyntaxException, IOException, InterruptedException, CommonServiceException {
    var timer =
        Timer.builder("userexit." + userExitData.getUserExitMetaData().getName() + ".postCall")
            .tag("orgId", userExitData.getUserExitConfigData().getOrgId())
            .publishPercentiles(0.99, 0.95)
            .publishPercentileHistogram()
            .register(meterRegistry);
    long startTime = System.currentTimeMillis();
    UserExitConfigDataDto configData = userExitData.getUserExitConfigData();
    Map<String, Object> requestObject = new HashMap<>();
    requestObject.put("input", inputData);
    requestObject.put("custom", customAttributeMap);
    Gson gson = new Gson();
    String requestString = gson.toJson(requestObject);
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(new URI(configData.getUrl()))
            .POST(HttpRequest.BodyPublishers.ofString(requestString))
            .header("Content-Type", "application/json")
            .header("Accept", "*/*")
            .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
            .build();
    HttpClient client = userExitUtil.getHttpClient();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    if (response.statusCode() >= 400) {
      throw new CommonServiceException(
          parseErrorMessages(userExitData.getUserExitMetaData().getName(), response.body()),
          HttpStatus.resolve(response.statusCode()),
          0,
          Map.of());
    }
    BaseResponse<G> baseResponse =
        (BaseResponse<G>) objectMapper.readValue(response.body(), classType);
    timer.record(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
    return baseResponse.getPayload();
  }

  private String parseErrorMessages(String userExitName, String errorBody) throws IOException {
    try {
      JsonNode messageJson = objectMapper.readTree(errorBody).get("message");
      if (messageJson != null && !messageJson.isNull()) {
        return String.format(
            "Unable to process request as %s failed with error: %s",
            userExitName, messageJson.asText());
      } else {
        return String.format("Unable to process request as %s failed", userExitName);
      }
    } catch (Exception e) {
      return String.format("Unable to process request as %s failed", userExitName);
    }
  }
}
