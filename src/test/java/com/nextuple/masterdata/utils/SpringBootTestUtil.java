package com.nextuple.masterdata.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextuple.AbstractSpringBootTest;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

/**
 * Utility class to perform container tests & perf testing.
 *
 * <p>It provides the following functionalities: 1. Parse input from resources 2. Initiate &
 * subscribe to kafka topics 3. Call rest endpoint 4. Poll the assertion 5. Poll messages from kafka
 * 6. Refresh the access token
 */
@Component
public class SpringBootTestUtil {
  public static final int POLL_DURATION_MS = 3000;
  public static final int DEFAULT_TIMEOUT_SECONDS = 30;
  public static final int TOKEN_EXPIRY_BUFFER_SECONDS = 180;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
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

  public SpringBootTestUtil() {
    refreshAccessToken();
  }

  // ===========================
  // Utils to access & parse the input
  // ===========================

  /**
   * Parses the input JSON data from a specified file and converts it into an object of the
   * specified class type.
   *
   * <p>This method reads the content from the given file path, which is expected to be in JSON
   * format, and then parses it into an instance of the provided class type.
   *
   * @param <T> the type of the object to which the input JSON data will be parsed.
   * @param filePath the path to the JSON file containing the input data to be parsed.
   * @param clazz the class type to which the JSON data should be converted.
   * @return an instance of the class type {@code clazz} with data parsed from the JSON file.
   */
  public <T> T parseClassFromJSON(String filePath, Class<T> clazz) {
    String input = readInput(filePath);
    return parseStringToClass(input, clazz);
  }

  /**
   * Parses the input JSON data from a specified file and converts it into an object of the
   * specified type.
   *
   * <p>This method reads the content from the given file path, which is expected to be in JSON
   * format, and then parses it into an instance of the provided type using {@link TypeReference}.
   * This is especially useful for complex types such as collections or parameterized types that
   * cannot be directly captured with a class type.
   *
   * @param <T> the type of the object to which the input JSON data will be parsed.
   * @param filePath the path to the JSON file containing the input data to be parsed.
   * @param clazz a {@link TypeReference} that defines the specific type into which the JSON data
   *     should be parsed. This allows handling of complex types like collections, maps, or generic
   *     types.
   * @return an instance of the type {@code T} with data parsed from the JSON file.
   */
  public <T> T parseClassFromJSON(String filePath, TypeReference<T> clazz) {
    String input = readInput(filePath);
    return parseStringToClass(input, clazz);
  }

  /**
   * Parses the given input string and converts it into an object of the specified class type.
   *
   * <p>This method uses an {@link ObjectMapper} to deserialize the input string (which is expected
   * to be in JSON format) into an instance of the provided class type.
   *
   * @param <T> the type of the object to which the input string will be parsed.
   * @param input the string containing the input data to be parsed. This is typically a
   *     JSON-formatted string.
   * @param clazz the class type to which the input string should be converted.
   * @return an instance of the class type {@code clazz} populated with data parsed from the input
   *     string.
   */
  public <T> T parseStringToClass(String input, Class<T> clazz) {
    try {
      return objectMapper.readValue(input, clazz);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error parsing input " + input, e);
    }
  }

  /**
   * Parses the given JSON string and converts it into an object of the specified type using {@link
   * TypeReference}.
   *
   * <p>This method uses an {@link ObjectMapper} to deserialize the input JSON string into an object
   * of the specified type. The {@link TypeReference} allows for handling complex types such as
   * collections or generic types that cannot be captured by the class type alone.
   *
   * @param <T> the type of the object to which the input JSON string will be parsed.
   * @param json the JSON-formatted string containing the data to be parsed.
   * @param typeReference a {@link TypeReference} that defines the specific type into which the JSON
   *     data should be parsed. This allows parsing of complex types like collections, maps, or
   *     other parameterized types.
   * @return an instance of the type {@code T} populated with data parsed from the input JSON
   *     string.
   */
  public <T> T parseStringToClass(String json, TypeReference<T> typeReference) {
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error parsing input " + json, e);
    }
  }

  /**
   * Reads the content of a file located in the resources directory and returns it as a string.
   *
   * <p>This method attempts to load the file using the class loader and reads its contents into a
   * string. It is expected that the file is available in the resources folder of the project.
   *
   * @param fileName the name of the file to be read, relative to the classpath.
   * @return the content of the file as a string.
   */
  public String readInput(String fileName) {
    ClassLoader classLoader = SpringBootTestUtil.class.getClassLoader();
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

  // ===========================
  // Utils to perform REST calls
  // ===========================

  /**
   * Calls a REST endpoint with the specified parameters and returns the response at the specified
   * output path.
   *
   * <p>This method sends an HTTP request to the given URL using the specified HTTP method and
   * request body. It expects a specific HTTP response status code and reads the response body to
   * extract the data at the given output path.
   *
   * @param <R> the type of the request body.
   * @param url the URL of the REST endpoint to call.
   * @param method the HTTP method to use for the request.
   * @param requestBody the body of the request to be sent.
   * @param outputPath the path in the response body from which to extract the data.
   * @param httpResponse the expected HTTP response status code.
   * @return the data extracted from the response body at the specified output path.
   */
  public <R> String callRestPayload(
      String url, HttpMethod method, R requestBody, String outputPath, int httpResponse)
      throws IOException {
    return callRestPayload(
        url, method, requestBody, outputPath, new LinkedMultiValueMap<>(), httpResponse);
  }

  /**
   * Calls a REST endpoint with the specified parameters and returns the response at the specified
   * output path.
   *
   * <p>This method sends an HTTP request to the given URL using the specified HTTP method and
   * request body. It expects a specific HTTP response status code and reads the response body to
   * extract the data at the given output path.
   *
   * @param <R> the type of the request body.
   * @param url the URL of the REST endpoint to call.
   * @param method the HTTP method to use for the request.
   * @param requestBody the body of the request to be sent.
   * @param outputPath the path in the response body from which to extract the data.
   * @return the data extracted from the response body at the specified output path.
   */
  public <R> String callRestPayload(String url, HttpMethod method, R requestBody, String outputPath)
      throws IOException {
    return callRestPayload(url, method, requestBody, outputPath, new LinkedMultiValueMap<>(), 200);
  }

  /**
   * Calls a REST endpoint with the specified parameters and returns the response at the specified
   * output path.
   *
   * <p>This method sends an HTTP request to the given URL using the specified HTTP method and
   * request body. It expects a specific HTTP response status code and reads the response body to
   * extract the data at the given output path.
   *
   * @param <R> the type of the request body.
   * @param url the URL of the REST endpoint to call.
   * @param method the HTTP method to use for the request.
   * @param requestBody the body of the request to be sent.
   * @param outputPath the path in the response body from which to extract the data.
   * @param headers the headers to be included in the request.
   * @return the data extracted from the response body at the specified output path.
   */
  public <R> String callRestPayload(
      String url,
      HttpMethod method,
      R requestBody,
      String outputPath,
      MultiValueMap<String, String> headers)
      throws IOException {
    return callRestPayload(url, method, requestBody, outputPath, headers, 200);
  }

  /**
   * Calls a REST endpoint with the specified parameters and returns the response at the specified
   * output path.
   *
   * <p>This method sends an HTTP request to the given URL using the specified HTTP method and
   * request body. It expects a specific HTTP response status code and reads the response body to
   * extract the data at the given output path.
   *
   * @param <R> the type of the request body.
   * @param url the URL of the REST endpoint to call.
   * @param method the HTTP method to use for the request.
   * @param requestBody the body of the request to be sent.
   * @param outputPath the path in the response body from which to extract the data.
   * @param headers the headers to be included in the request.
   * @param httpResponse the expected HTTP response status code.
   * @return the data extracted from the response body at the specified output path.
   */
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
    System.out.println("Response: " + responseBody.toString());
    return responseBody.path(outputPath).toString();
  }

  /**
   * Calls a REST endpoint with the specified parameters and returns the response.
   *
   * <p>This method sends an HTTP request to the given URL using the specified HTTP method and
   * request body. It expects a specific HTTP response status code and returns the response body as
   * a string.
   *
   * @param <R> the type of the request body.
   * @param url the URL of the REST endpoint to call.
   * @param method the HTTP method to use for the request.
   * @param requestBody the body of the request to be sent.
   * @param headers the headers to be included in the request.
   * @param httpResponse the expected HTTP response status code.
   * @return the response body as a string.
   */
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
    Assertions.assertEquals(
        httpResponse,
        response.getStatusCode().value(),
        "Error in calling the " + url + " endpoint: " + url + response.getBody());
    return response.getBody();
  }

  // ===========================
  // Utils to poll the assertion
  // ===========================

  /**
   * Polls the specified function and asserts the conditions on the result.
   *
   * <p>This method repeatedly polls the given function at regular intervals until the specified
   * conditions are met. It allows for a maximum number of attempts and a timeout duration to be
   * specified.
   *
   * @param <T> the type of the result returned by the polling function.
   * @param pollingFunction the function to be polled for results.
   * @param assertConditions the conditions to be asserted on the result.
   * @return the final result obtained after polling.
   */
  public <T> T pollAndAssert(Supplier<T> pollingFunction, Consumer<T> assertConditions) {
    return pollAndAssert(pollingFunction, 3, 60, assertConditions);
  }

  /**
   * Polls the specified function and asserts the conditions on the result.
   *
   * <p>This method repeatedly polls the given function at regular intervals until the specified
   * conditions are met. It allows for a maximum number of attempts and a timeout duration to be
   * specified.
   *
   * @param <T> the type of the result returned by the polling function.
   * @param pollingFunction the function to be polled for results.
   * @param pollIntervalSeconds the interval between each poll attempt in seconds.
   * @param timeoutSeconds the maximum duration for which polling should be attempted.
   * @param assertConditions the conditions to be asserted on the result.
   * @return the final result obtained after polling.
   */
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

  // ===========================
  // Utils to poll messages from Kafka
  // ===========================

  /**
   * Subscribes to the specified Kafka topics and starts polling messages.
   *
   * <p>This method subscribes to the given list of Kafka topics and starts polling messages from
   * the consumer. It creates a new Kafka consumer instance and subscribes to the specified topics.
   *
   * @param topics the list of Kafka topics to which the consumer should subscribe.
   */
  public void subscribeToTopics(List<String> topics) {
    unsubscribeFromTopics();
    Properties consumerProps = new Properties();
    consumerProps.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        AbstractSpringBootTest.kafka.getBootstrapServers());
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-container-group");
    consumerProps.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProps.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumer = new KafkaConsumer<>(consumerProps);
    consumer.subscribe(topics);
  }

  /**
   * Unsubscribes from the currently subscribed Kafka topics.
   *
   * <p>This method unsubscribes from the currently subscribed Kafka topics and closes the consumer
   * instance.
   */
  public void unsubscribeFromTopics() {
    if (consumer != null) {
      consumer.unsubscribe();
      consumer.close();
    }
    consumer = null;
  }

  /**
   * Polls the specified number of messages from the Kafka consumer.
   *
   * <p>This method polls the Kafka consumer for the specified number of messages and deserializes
   * them into objects of the specified class type. It waits for a maximum duration of {@code
   * DEFAULT_TIMEOUT_SECONDS} seconds for the messages to be received.
   *
   * @param <T> the type of the object to which the Kafka messages will be deserialized.
   * @param count the number of messages to poll from the Kafka consumer.
   * @param typeReference a {@link TypeReference} that defines the specific type into which the
   *     Kafka messages should be deserialized.
   * @return a list of objects of type {@code T} deserialized from the Kafka messages.
   */
  public <T> List<T> pollKafkaConsumer(int count, TypeReference<T> typeReference) {
    return pollKafkaConsumer(count, typeReference, null);
  }

  /**
   * Polls the specified number of messages from the Kafka consumer.
   *
   * <p>This method polls the Kafka consumer for the specified number of messages and deserializes
   * them into objects of the specified class type. It waits for a maximum duration of {@code
   * DEFAULT_TIMEOUT_SECONDS} seconds for the messages to be received.
   *
   * @param <T> the type of the object to which the Kafka messages will be deserialized.
   * @param count the number of messages to poll from the Kafka consumer.
   * @param typeReference a {@link TypeReference} that defines the specific type into which the
   *     Kafka messages should be deserialized.
   * @param topic the Kafka topic from which to poll the messages. Can be {@code null} to poll from
   *     all topics.
   * @return a list of objects of type {@code T} deserialized from the Kafka messages.
   */
  public <T> List<T> pollKafkaConsumer(int count, TypeReference<T> typeReference, String topic) {
    return pollKafkaConsumer(count, typeReference, topic, DEFAULT_TIMEOUT_SECONDS);
  }

  /**
   * Polls the specified number of messages from the Kafka consumer.
   *
   * <p>This method polls the Kafka consumer for the specified number of messages and deserializes
   * them into objects of the specified class type. It waits for a maximum duration of {@code
   * timeoutSeconds} for the messages to be received.
   *
   * @param <T> the type of the object to which the Kafka messages will be deserialized.
   * @param count the number of messages to poll from the Kafka consumer.
   * @param typeReference a {@link TypeReference} that defines the specific type into which the
   *     Kafka messages should be deserialized.
   * @param topic the Kafka topic from which to poll the messages. Can be {@code null} to poll from
   *     all topics.
   * @param timeoutSeconds the maximum duration for which polling should be attempted.
   * @return a list of objects of type {@code T} deserialized from the Kafka messages.
   */
  public <T> List<T> pollKafkaConsumer(
      int count, TypeReference<T> typeReference, String topic, int timeoutSeconds) {
    List<T> result = new ArrayList<>();
    long startTime = System.currentTimeMillis();
    while ((System.currentTimeMillis() - startTime) < timeoutSeconds * 1000L) {
      var records = consumer.poll(java.time.Duration.ofMillis(POLL_DURATION_MS));
      if (!records.isEmpty()) {
        for (ConsumerRecord<String, String> record : records) {
          if ((topic == null) || record.topic().equals(topic)) {
            result.add(parseStringToClass(record.value(), typeReference));
          }
        }
      }
      if (result.size() >= count) {
        break;
      }
    }
    return result;
  }

  // ===========================
  // Utils to refresh the access token
  // ===========================

  /**
   * Refreshes the access token by calling the Keycloak token endpoint.
   *
   * <p>This method refreshes the access token by calling the Keycloak token endpoint with the
   * configured credentials. It retrieves the new access token and its expiration time, and updates
   * the internal state accordingly.
   */
  public void refreshAccessToken() {
    try {
      if (accessToken == null
          || expiresAt - TOKEN_EXPIRY_BUFFER_SECONDS <= Instant.now().getEpochSecond()) {
        scheduler.shutdown();

        String data =
            "grant_type=password&client_id="
                + tenantClientId
                + "&username="
                + username
                + "&password="
                + password;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        String response = callRest(keycloakUrl, HttpMethod.POST, data, headers, 200);
        JsonNode responseBody = objectMapper.readTree(response.toString());
        accessToken = responseBody.path("access_token").asText();
        expiresAt = Instant.now().getEpochSecond() + responseBody.path("expires_in").asLong();
        scheduler.schedule(
            this::refreshAccessToken,
            expiresAt - Instant.now().getEpochSecond() - TOKEN_EXPIRY_BUFFER_SECONDS,
            TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
