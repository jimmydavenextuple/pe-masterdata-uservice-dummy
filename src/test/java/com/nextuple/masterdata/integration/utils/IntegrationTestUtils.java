package com.nextuple.masterdata.integration.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@Component
public class IntegrationTestUtils {
  public ObjectMapper objectMapper = new ObjectMapper();
  public TestRestTemplate testRestTemplate = new TestRestTemplate();
  public KafkaConsumer<String, String> consumer = null;
  public String accessToken = null;
  public long expiresAt = 0;

  @Value("${test-container.keycloak.token-url}")
  private String keycloakUrl;

  @Value("${test-container.keycloak.tenant-client-id}")
  private String tenantClientId;

  @Value("${test-container.keycloak.username}")
  private String username;

  @Value("${test-container.keycloak.password}")
  private String password;

  public void TestUtils() {
    refreshAccessToken();
  }

  /*
   Utils to access & parse the input
  */

  public <T> T parseInputFromResources(String fileName, Class<T> clazz) {
    String input = readInput(fileName);
    return parseStringToClass(input, clazz);
  }

  public <T> T parseInputFromResources(String fileName, TypeReference<T> clazz) {
    String input = readInput(fileName);
    return parseStringToClass(input, clazz);
  }

  public <T> T parseStringToClass(String input, Class<T> clazz) {
    try {
      return objectMapper.readValue(input, clazz);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error parsing input " + input, e);
    }
  }

  public <T> T parseStringToClass(String json, TypeReference<T> typeReference) {
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error parsing input " + json, e);
    }
  }

  public String readInput(String fileName) {
    ClassLoader classLoader = IntegrationTestUtils.class.getClassLoader();
    URL resource = classLoader.getResource(fileName);
    if (resource == null) {
      throw new IllegalArgumentException("file not found! " + fileName);
    } else {
      try {
        File f = new File(resource.toURI());
        return new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));
      } catch (Exception e) {
        throw new IllegalArgumentException("file not found! " + fileName);
      }
    }
  }

  /*
   Utils to perform a rest call
  */

  public <R> String callRestPayload(
      String url, HttpMethod method, R requestBody, String outputPath, int httpResponse)
      throws IOException {
    return callRestPayload(
        url, method, requestBody, outputPath, new LinkedMultiValueMap<>(), httpResponse);
  }

  public <R> String callRestPayload(String url, HttpMethod method, R requestBody, String outputPath)
      throws IOException {
    return callRestPayload(url, method, requestBody, outputPath, new LinkedMultiValueMap<>(), 200);
  }

  public <R> String callRestPayload(
      String url,
      HttpMethod method,
      R requestBody,
      String outputPath,
      MultiValueMap<String, String> headers)
      throws IOException {
    return callRestPayload(url, method, requestBody, outputPath, headers, 200);
  }

  public <R> String callRestPayload(
      String url,
      HttpMethod method,
      R requestBody,
      String outputPath,
      MultiValueMap<String, String> headers,
      int httpResponse)
      throws IOException {
    headers.add("Accept", "application/json");
    JsonNode responseBody =
        objectMapper.readTree(callRest(url, method, requestBody, headers, httpResponse));
    return responseBody.path(outputPath).toString();
  }

  public <R> String callRest(
      String url,
      HttpMethod method,
      R requestBody,
      MultiValueMap<String, String> headers,
      int httpResponse) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + accessToken);
    httpHeaders.add("x-tenant-id", "NEXTUPLE_GR");
    httpHeaders.addAll(headers);

    ResponseEntity<String> response =
        testRestTemplate.exchange(
            url, method, new HttpEntity<>(requestBody, httpHeaders), String.class);
    System.out.println("Response: " + response.getBody());
    Assertions.assertEquals(
        httpResponse,
        response.getStatusCode().value(),
        "Error in calling the " + url + " endpoint: " + url + response.getBody());
    return response.getBody();
  }

  /*
   Utils to poll the assertion from database
  */
  public <T> T pollAndAssert(Supplier<T> pollingFunction, Consumer<T> assertConditions) {
    return pollAndAssert(pollingFunction, 3, 60, assertConditions);
  }

  public <T> T pollAndAssert(
      Supplier<T> pollingFunction,
      long pollIntervalSeconds,
      long timeoutSeconds,
      Consumer<T> assertConditions) {
    AtomicReference<T> result = new AtomicReference<>();
    Awaitility.await()
        .pollInterval(Duration.ofSeconds(pollIntervalSeconds))
        .atMost(timeoutSeconds, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              result.set(pollingFunction.get());
              System.out.println("Polling result: " + result.get());
              assertConditions.accept(result.get());
            });
    return result.get();
  }

  /*
   Utils to subscribe to kafka topics
  */

  public void subscribeToTopics(List<String> topics, String bootstrapServers) {
    Properties consumerProps = new Properties();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-container-group");
    consumerProps.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProps.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumer = new KafkaConsumer<>(consumerProps);
    consumer.subscribe(topics);
  }

  public void unsubscribeFromTopics() {
    if (consumer != null) {
      consumer.unsubscribe();
      consumer.close();
    }
    consumer = null;
  }

  public <T> List<T> pollKafkaConsumer(int count, TypeReference<T> typeReference) {
    return pollKafkaConsumer(count, typeReference, 30);
  }

  public <T> List<T> pollKafkaConsumer(
      int count, TypeReference<T> typeReference, int timeoutSeconds) {
    List<T> result = new ArrayList<>();
    long startTime = System.currentTimeMillis();
    while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000L) {
      var records = consumer.poll(java.time.Duration.ofMillis(3000));
      if (!records.isEmpty()) {
        for (ConsumerRecord<String, String> record : records) {
          result.add(parseStringToClass(record.value(), typeReference));
        }
      }
      if (result.size() >= count) {
        break;
      }
    }
    return result;
  }

  /*
     Utils to poll the assertion from kafka
  */

  public void refreshAccessToken() {
    try {
      if (accessToken == null || expiresAt - (3 * 60) < Instant.now().getEpochSecond()) {

        String data =
            "grant_type=password&client_id="
                + tenantClientId
                + "&username="
                + username
                + "&password="
                + password;

        URL url = new URL(keycloakUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
          byte[] input = data.getBytes("utf-8");
          os.write(input, 0, input.length);
        }

        try (BufferedReader in =
            new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
          StringBuilder response = new StringBuilder();
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }

          System.out.println("accessToken: " + response);
          JsonNode responseBody = objectMapper.readTree(response.toString());
          accessToken = responseBody.path("access_token").asText();
          expiresAt = Instant.now().getEpochSecond() + responseBody.path("expires_in").asLong();
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
