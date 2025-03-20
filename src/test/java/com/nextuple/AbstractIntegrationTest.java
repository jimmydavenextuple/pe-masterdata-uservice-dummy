package com.nextuple;

import com.nextuple.masterdata.PeMasterDataUserviceApplication;
import com.nextuple.masterdata.integration.utils.IntegrationTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
    classes = PeMasterDataUserviceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
      "spring.datasource.url=jdbc:tc:postgresql:17-alpine:///ips?TC_INITSCRIPT=file:src/test/resources/db_setup.sql",
      "server.port=8080"
    })
@ActiveProfiles("testcontainer")
public abstract class AbstractIntegrationTest {
  public static KafkaContainer kafka =
      new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));
  @Autowired public IntegrationTestUtils integrationTestUtils;

  @BeforeEach
  public void setup() {
    integrationTestUtils.refreshAccessToken();
  }

  @AfterEach
  public void tearDown() {
    integrationTestUtils.unsubscribeFromTopics();
  }

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    kafka.start();
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
  }
}
