package com.nextuple;

import com.nextuple.masterdata.PeMasterDataUserviceApplication;
import com.nextuple.masterdata.utils.SpringBootTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Abstract class for spring boot test
 *
 * <p>It starts the spring boot application on defined port and uses testcontainers to start kafka &
 * postgres containers. It also provides the utility class to interact with the application.
 *
 * <p>Any common config & method for proxy tests can be added here.
 */
@DirtiesContext
@SpringBootTest(
    classes = PeMasterDataUserviceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
      "spring.datasource.url=jdbc:tc:postgresql:17-alpine:///pe?TC_INITSCRIPT=file:src/test/resources/db_setup.sql",
      "server.port=8080"
    })
@ActiveProfiles("proxy")
public abstract class AbstractSpringBootTest {

  /** Kafka container */
  public static KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

  /** Check util possibilities at: {@link SpringBootTestUtil} */
  @Autowired public SpringBootTestUtil util;

  /**
   * Starts the kafka container and sets the bootstrap servers for kafka and camel.
   *
   * @param registry dynamic property registry
   */
  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    kafka.start();
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
  }
}
