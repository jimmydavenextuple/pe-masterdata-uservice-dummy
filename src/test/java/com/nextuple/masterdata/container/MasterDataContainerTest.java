package com.nextuple.masterdata.container;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.masterdata.AbstractContainerTest;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.node.domain.inbound.NodeRequest;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.node.persistence.domain.NodeDomainDto;
import com.nextuple.node.persistence.service.impl.NodePersistenceServiceImpl;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.vendor.consumer.dto.VendorFeedDto;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.service.impl.VendorPersistenceServiceImpl;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MasterDataContainerTest extends AbstractContainerTest {

  @Autowired NodePersistenceServiceImpl nodePersistenceServiceImpl;

  @Autowired VendorPersistenceServiceImpl vendorPersistenceServiceImpl;

  @Value("${master-data.vendor.topic-names:null}")
  private String vendorFeedTopic;

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
    // Fetch the input from the resources
    NodeRequest nodeRequestBody = util.parseClassFromJSON(nodeRequest, NodeRequest.class);
    NodeDomainDto expectedNodeDomainDto =
        util.parseClassFromJSON(nodeDomainDto, NodeDomainDto.class);
    NodeResponse expectedNodeResponse = util.parseClassFromJSON(nodeResponse, NodeResponse.class);

    // Call the REST API
    String res =
        util.callRestPayload(
            "http://localhost:8080/node", HttpMethod.POST, nodeRequestBody, "payload", 200);
    NodeResponse convertedObject = util.parseStringToClass(res, NodeResponse.class);

    // Assert the REST response
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
    Assertions.assertEquals(3, convertedObject.getCustomAttributes().size());

    // Poll the database and assert the values
    util.pollAndAssert(
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
        util.parseClassFromJSON("expected/node/create-node-response.json", NodeResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/node/nodeId-001/NEXTUPLE_GR", HttpMethod.GET, null, "payload");
    NodeResponse convertedObject = util.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
  }

  @Test
  @Order(3)
  @DisplayName("Happy Path: Update Node Details")
  void updateNodeDetailsWithValidInput() throws IOException {
    NodeRequest nodeRequestBody =
        util.parseClassFromJSON("input/node/update-node.json", NodeRequest.class);
    NodeDomainDto expectedNodeDomainDto =
        util.parseClassFromJSON("expected/node/update-node-db-value.json", NodeDomainDto.class);
    NodeResponse expectedNodeResponse =
        util.parseClassFromJSON("expected/node/update-node-response.json", NodeResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/node/nodeId-001/NEXTUPLE_GR",
            HttpMethod.PUT,
            nodeRequestBody,
            "payload");
    NodeResponse convertedObject = util.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);
    Assertions.assertEquals(2, convertedObject.getCustomAttributes().size());

    util.pollAndAssert(
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
    // Fetch the input from the resources
    FeedRequest<MasterDataIngestionDto<NodeFeedDto>> nodeRequestBody =
        util.parseClassFromJSON(
            "input/node/node-feed.json",
            new TypeReference<FeedRequest<MasterDataIngestionDto<NodeFeedDto>>>() {});
    List<NodeDomainDto> expectedNodeResponse =
        util.parseClassFromJSON(
            "expected/node/node-feed.json", new TypeReference<List<NodeDomainDto>>() {});

    // Initialize the Kafka consumer & subscribe to the topic
    util.subscribeToTopics(List.of(nodeFeedTopic));

    // Call the REST API
    util.callRestPayload(
        "http://localhost:8080/ingest-data/nodes", HttpMethod.POST, nodeRequestBody, "payload");

    // Poll the Kafka consumer and assert the values
    List<BatchRequest<NodeFeedDto>> records =
        util.pollKafkaConsumer(
            nodeRequestBody.getData().size(),
            new TypeReference<BatchRequest<NodeFeedDto>>() {},
            nodeFeedTopic);

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

    // Poll the database and assert the values
    util.pollAndAssert(
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
        util.parseClassFromJSON("expected/node/delete-response.json", NodeResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/node/nodeId-002/NEXTUPLE_GR",
            HttpMethod.DELETE,
            null,
            "payload");
    NodeResponse convertedObject = util.parseStringToClass(res, NodeResponse.class);
    Assertions.assertEquals(expectedNodeResponse, convertedObject);

    util.pollAndAssert(
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
    String response =
        util.callRestPayload(
            "http://localhost:8080/node/nodeId-002/NEXTUPLE_GR",
            HttpMethod.GET,
            null,
            "message",
            404);
    Assertions.assertEquals("\"Node not found with given details\"", response);
  }

  @Order(7)
  @DisplayName("Test case to create multiple new vendors")
  @ParameterizedTest(name = "Create Vendor with {0}")
  @CsvSource(
      value = {
        "input/vendor/create-vendor.json, expected/vendor/create-vendor-db-value.json, expected/vendor/create-vendor-response.json",
        "input/vendor/create-vendor-2.json, expected/vendor/create-vendor-db-value-2.json, expected/vendor/create-vendor-response-2.json",
        "input/vendor/create-vendor-3.json, expected/vendor/create-vendor-db-value-3.json, expected/vendor/create-vendor-response-3.json"
      })
  void createVendorWithValidInput(
      String vendorRequest, String vendorDomainDto, String vendorResponse) throws IOException {
    // Fetch the input from the resources
    VendorRequest vendorRequestBody = util.parseClassFromJSON(vendorRequest, VendorRequest.class);
    VendorDomainDto expectedVendorDomainDto =
        util.parseClassFromJSON(vendorDomainDto, VendorDomainDto.class);
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON(vendorResponse, VendorResponse.class);

    // Call the REST API
    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor", HttpMethod.POST, vendorRequestBody, "payload", 200);
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);

    // Assert the REST response
    Assertions.assertEquals(expectedVendorResponse, convertedObject);

    // Poll the database and assert the values
    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        vendorRequestBody.getVendorId(), vendorRequestBody.getOrgId())),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedVendorDomainDto, input.get());
        });
  }

  @Test
  @Order(8)
  @DisplayName("Get Details of an existing Vendor")
  void getVendorDetailsWithValidInput() throws IOException {
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON(
            "expected/vendor/create-vendor-response.json", VendorResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor/Vendor-001/NEXTUPLE_GR", HttpMethod.GET, null, "payload");
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);
    Assertions.assertEquals(expectedVendorResponse, convertedObject);
  }

  @Test
  @Order(9)
  @DisplayName("Update the details of an existing Vendor")
  void updateVendorDetailsWithValidInput() throws IOException {
    VendorRequest vendorRequestBody =
        util.parseClassFromJSON("input/vendor/update-vendor.json", VendorRequest.class);
    VendorDomainDto expectedVendorDomainDto =
        util.parseClassFromJSON(
            "expected/vendor/update-vendor-db-value.json", VendorDomainDto.class);
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON(
            "expected/vendor/update-vendor-response.json", VendorResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor/Vendor-001/NEXTUPLE_GR",
            HttpMethod.PUT,
            vendorRequestBody,
            "payload");
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);
    Assertions.assertEquals(expectedVendorResponse, convertedObject);

    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        "Vendor-001", "NEXTUPLE_GR")),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedVendorDomainDto, input.get());
        });
  }

  @Test
  @Order(10)
  @DisplayName("Perform Create, Update and Delete Vendor using MDI")
  void createVendorFeedIngestionWithValidInput() throws IOException {
    // Fetch the input from the resources
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> vendorRequestBody =
        util.parseClassFromJSON(
            "input/vendor/vendor-feed.json",
            new TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>() {});
    VendorDomainDto expectedVendorResponse1 =
        util.parseClassFromJSON("expected/vendor/vendor-feed-1.json", VendorDomainDto.class);
    VendorDomainDto expectedVendorResponse2 =
        util.parseClassFromJSON("expected/vendor/vendor-feed-2.json", VendorDomainDto.class);

    // Initialize the Kafka consumer & subscribe to the topic
    util.subscribeToTopics(List.of(vendorFeedTopic));

    // Call the REST API
    util.callRestPayload(
        "http://localhost:8080/ingest-data/vendor", HttpMethod.POST, vendorRequestBody, "payload");

    // Poll the Kafka consumer and assert the values
    List<BatchRequest<VendorFeedDto>> records =
        util.pollKafkaConsumer(
            vendorRequestBody.getData().size(),
            new TypeReference<BatchRequest<VendorFeedDto>>() {},
            vendorFeedTopic);

    Assertions.assertEquals(vendorRequestBody.getData().size(), records.size());
    Assertions.assertEquals(
        vendorRequestBody.getData().stream()
            .map(input -> input.getAction().name() + input.getPayload().getVendorId())
            .sorted()
            .toList(),
        records.stream()
            .map(input -> input.getAction().name() + input.getPayload().getVendorId())
            .sorted()
            .toList());
    Assertions.assertEquals(
        vendorRequestBody.getData().stream()
            .map(MasterDataIngestionDto::getPayload)
            .sorted(Comparator.comparing(VendorFeedDto::getVendorId))
            .toList(),
        records.stream()
            .map(BatchRequest::getPayload)
            .sorted(Comparator.comparing(VendorFeedDto::getVendorId))
            .toList());

    // Poll the database and assert the values
    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        "Vendor-10", "NEXTUPLE_GR")),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedVendorResponse1, input.get());
        });

    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        "Vendor-001", "NEXTUPLE_GR")),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedVendorResponse2, input.get());
        });
    String response =
        util.callRestPayload(
            "http://localhost:8080/vendor/Vendor-003/NEXTUPLE_GR",
            HttpMethod.GET,
            null,
            "message",
            404);
    Assertions.assertEquals("\"Vendor not found with given details\"", response);
  }

  @Test
  @DisplayName("Happy Path: Delete a Vendor that exists in the db")
  @Order(11)
  void deleteVendorWithValidInput() throws IOException {
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON("expected/vendor/delete-response.json", VendorResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor/Vendor-002/NEXTUPLE_GR",
            HttpMethod.DELETE,
            null,
            "payload");
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);
    Assertions.assertEquals(expectedVendorResponse, convertedObject);

    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        "vendorId-002", "NEXTUPLE_GR")),
        (input) -> Assertions.assertTrue(input.isEmpty()));
  }

  @Test
  @Order(12)
  @DisplayName("Attempt getting Vendor Details of the Deleted Vendor")
  void getVendorDetailsWithInvalidInput() throws IOException {
    String response =
        util.callRestPayload(
            "http://localhost:8080/vendor/Vendor-002/NEXTUPLE_GR",
            HttpMethod.GET,
            null,
            "message",
            404);
    Assertions.assertEquals("\"Vendor not found with given details\"", response);
  }
}
