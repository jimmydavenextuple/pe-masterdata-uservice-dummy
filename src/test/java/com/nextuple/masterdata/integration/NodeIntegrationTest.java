package com.nextuple.masterdata.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.AbstractIntegrationTest;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.service.impl.NodePersistenceServiceImpl;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NodeIntegrationTest extends AbstractIntegrationTest {
  @Autowired KafkaTemplate<String, String> kafkaTemplate;
  @Autowired NodePersistenceServiceImpl nodePersistenceServiceImpl;

  @Value("${master-data.node.topic-names:null}")
  private String nodeFeedTopic;

  @Order(1)
  @DisplayName("Happy Path: Create Node")
  @ParameterizedTest(name = "Create Node with {0}")
  @CsvSource(
      value = {
        "input/node/create-node.json, expected/node/create-node-db-value.json, expected/node/create-node-response.json",
        "input/node/create-node-2.json, expected/node/create-node-db-value-2.json, expected/node/create-node-response-2.json"
      })
  void createNodeWithValidInput(String nodeRequest, String nodeDomainDto, String nodeResponse)
      throws IOException {
    NodeRequest nodeRequestBody =
        integrationTestUtils.parseInputFromResources(nodeRequest, NodeRequest.class);
    NodeDomainDto expectedNodeDomainDto =
        integrationTestUtils.parseInputFromResources(nodeDomainDto, NodeDomainDto.class);
    NodeResponse expectedNodeResponse =
        integrationTestUtils.parseInputFromResources(nodeResponse, NodeResponse.class);

    String res =
        integrationTestUtils.callRestPayload(
            "http://localhost:8080/node", HttpMethod.POST, nodeRequestBody, "payload");
    NodeResponse convertedObject = integrationTestUtils.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
    Assertions.assertEquals(3, convertedObject.getCustomAttributes().size());

    integrationTestUtils.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    nodePersistenceServiceImpl.findNodeByNodeIdAndOrgId(
                        nodeRequestBody.getNodeId(), nodeRequestBody.getOrgId())),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedNodeDomainDto, input.get());
        });
  }

  @Test
  @Order(2)
  @DisplayName("Happy Path: Get Node Details")
  void getNodeDetailsWithValidInput() throws IOException {
    NodeResponse expectedNodeResponse =
        integrationTestUtils.parseInputFromResources(
            "expected/node/create-node-response.json", NodeResponse.class);

    String res =
        integrationTestUtils.callRestPayload(
            "http://localhost:8080/node/nodeId-001/NEXTUPLE_GR", HttpMethod.GET, null, "payload");
    NodeResponse convertedObject = integrationTestUtils.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
  }

  @Test
  @Order(3)
  @DisplayName("Happy Path: Update Node Details")
  void updateNodeDetailsWithValidInput() throws IOException {
    NodeRequest nodeRequestBody =
        integrationTestUtils.parseInputFromResources(
            "input/node/update-node.json", NodeRequest.class);
    NodeDomainDto expectedNodeDomainDto =
        integrationTestUtils.parseInputFromResources(
            "expected/node/update-node-db-value.json", NodeDomainDto.class);
    NodeResponse expectedNodeResponse =
        integrationTestUtils.parseInputFromResources(
            "expected/node/update-node-response.json", NodeResponse.class);

    String res =
        integrationTestUtils.callRestPayload(
            "http://localhost:8080/node/nodeId-001/NEXTUPLE_GR",
            HttpMethod.PUT,
            nodeRequestBody,
            "payload");
    NodeResponse convertedObject = integrationTestUtils.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
    Assertions.assertEquals(2, convertedObject.getCustomAttributes().size());

    integrationTestUtils.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    nodePersistenceServiceImpl.findNodeByNodeIdAndOrgId(
                        "nodeId-001", "NEXTUPLE_GR")),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedNodeDomainDto, input.get());
        });
  }

  @Test
  @Order(4)
  @DisplayName("Happy Path: Create, Update and Delete Node with MDI")
  void createNodeFeedIngestionWithValidInput() throws IOException {
    FeedRequest<MasterDataIngestionDto<NodeFeedDto>> nodeRequestBody =
        integrationTestUtils.parseInputFromResources(
            "input/node/node-feed.json",
            new TypeReference<FeedRequest<MasterDataIngestionDto<NodeFeedDto>>>() {});
    List<NodeDomainDto> expectedNodeResponse =
        integrationTestUtils.parseInputFromResources(
            "expected/node/node-feed.json", new TypeReference<List<NodeDomainDto>>() {});

    integrationTestUtils.subscribeToTopics(List.of(nodeFeedTopic), kafka.getBootstrapServers());

    integrationTestUtils.callRestPayload(
        "http://localhost:8080/ingest-data/nodes", HttpMethod.POST, nodeRequestBody, "payload");

    List<BatchRequest<NodeFeedDto>> records =
        integrationTestUtils.pollKafkaConsumer(
            nodeRequestBody.getData().size(), new TypeReference<BatchRequest<NodeFeedDto>>() {});
    Assertions.assertEquals(nodeRequestBody.getData().size(), records.size());
    Assertions.assertEquals(
        nodeRequestBody.getData().stream()
            .map(input -> input.getAction().name() + input.getPayload().getNodeId())
            .sorted()
            .toList(),
        records.stream()
            .map(input -> input.getAction().name() + input.getPayload().getNodeId())
            .sorted()
            .toList());
    Assertions.assertEquals(
        nodeRequestBody.getData().stream()
            .map(MasterDataIngestionDto::getPayload)
            .sorted(Comparator.comparing(NodeFeedDto::getNodeId))
            .toList(),
        records.stream()
            .map(BatchRequest::getPayload)
            .sorted(Comparator.comparing(NodeFeedDto::getNodeId))
            .toList());

    integrationTestUtils.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(() -> nodePersistenceServiceImpl.getAllNodeEntities(20)),
        (input) -> {
          Assertions.assertEquals(expectedNodeResponse.size(), input.size());
          Assertions.assertEquals(
              expectedNodeResponse.stream()
                  .sorted(Comparator.comparing(NodeDomainDto::getNodeId))
                  .toList(),
              input.stream().sorted(Comparator.comparing(NodeDomainDto::getNodeId)).toList());
        });
  }

  @Test
  @DisplayName("Happy Path: Delete Node")
  @Order(5)
  void deleteNodeWithValidInput() throws IOException {
    NodeResponse expectedNodeResponse =
        integrationTestUtils.parseInputFromResources(
            "expected/node/delete-response.json", NodeResponse.class);

    String res =
        integrationTestUtils.callRestPayload(
            "http://localhost:8080/node/nodeId-002/NEXTUPLE_GR",
            HttpMethod.DELETE,
            null,
            "payload");
    NodeResponse convertedObject = integrationTestUtils.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);

    integrationTestUtils.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    nodePersistenceServiceImpl.findNodeByNodeIdAndOrgId(
                        "nodeId-002", "NEXTUPLE_GR")),
        (input) -> Assertions.assertTrue(input.isEmpty()));
  }

  @Test
  @Order(6)
  @DisplayName("Happy Path: Get Node Details of a Deleted Node")
  void getNodeDetailsWithInvalidInput() throws IOException {
    integrationTestUtils.callRestPayload(
        "http://localhost:8080/node/nodeId-002/NEXTUPLE_GR", HttpMethod.GET, null, "", 404);
  }
}
