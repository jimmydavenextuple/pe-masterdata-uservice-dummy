package com.nextuple.masterdata.container;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.masterdata.AbstractContainerTest;
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
class VendorContainerTest extends AbstractContainerTest {

  @Autowired VendorPersistenceServiceImpl vendorPersistenceServiceImpl;

  @Value("${master-data.vendor.topic-names:null}")
  private String vendorFeedTopic;

  @Order(1)
  @DisplayName("Happy Path: Create Vendor")
  @ParameterizedTest(name = "Create Vendor with {0}")
  @CsvSource(
      value = {
        "input/vendor/create-vendor.json, expected/vendor/create-vendor-db-value.json, expected/vendor/create-vendor-response.json",
        "input/vendor/create-vendor-2.json, expected/vendor/create-vendor-db-value-2.json, expected/vendor/create-vendor-response-2.json"
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
    Assertions.assertEquals(3, convertedObject.getCustomAttributes().size());

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
  @Order(2)
  @DisplayName("Happy Path: Get Vendor Details")
  void getVendorDetailsWithValidInput() throws IOException {
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON(
            "expected/vendor/create-vendor-response.json", VendorResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor/vendorId-001/NEXTUPLE_GR",
            HttpMethod.GET,
            null,
            "payload");
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);
    Assertions.assertEquals(expectedVendorResponse, convertedObject);
  }

  @Test
  @Order(3)
  @DisplayName("Happy Path: Update Vendor Details")
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
            "http://localhost:8080/vendor/vendorId-001/NEXTUPLE_GR",
            HttpMethod.PUT,
            vendorRequestBody,
            "payload");
    VendorResponse convertedObject = util.parseStringToClass(res, VendorResponse.class);
    Assertions.assertEquals(expectedVendorResponse, convertedObject);
    Assertions.assertEquals(2, convertedObject.getCustomAttributes().size());

    util.pollAndAssert(
        () ->
            Assertions.assertDoesNotThrow(
                () ->
                    vendorPersistenceServiceImpl.findVendorByVendorIdAndOrgId(
                        "vendorId-001", "NEXTUPLE_GR")),
        (input) -> {
          Assertions.assertTrue(input.isPresent());
          Assertions.assertEquals(expectedVendorDomainDto, input.get());
        });
  }

  @Test
  @Order(4)
  @DisplayName("Happy Path: Create, Update and Delete Vendor with MDI")
  void createVendorFeedIngestionWithValidInput() throws IOException {
    // Fetch the input from the resources
    FeedRequest<MasterDataIngestionDto<VendorFeedDto>> vendorRequestBody =
        util.parseClassFromJSON(
            "input/vendor/vendor-feed.json",
            new TypeReference<FeedRequest<MasterDataIngestionDto<VendorFeedDto>>>() {});
    List<VendorDomainDto> expectedVendorResponse =
        util.parseClassFromJSON(
            "expected/vendor/vendor-feed.json", new TypeReference<List<VendorDomainDto>>() {});

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
                    vendorPersistenceServiceImpl.getVendorByOrgId(
                        "NEXTUPLE_GR", 1, 10, "vendorId", "ASC")),
        (input) -> {
          Assertions.assertEquals(expectedVendorResponse.size(), input.getSize());
          Assertions.assertEquals(
              expectedVendorResponse.stream()
                  .sorted(Comparator.comparing(VendorDomainDto::getVendorId))
                  .toList(),
              input.stream().sorted(Comparator.comparing(VendorDomainDto::getVendorId)).toList());
        });
  }

  @Test
  @DisplayName("Happy Path: Delete Vendor")
  @Order(5)
  void deleteVendorWithValidInput() throws IOException {
    VendorResponse expectedVendorResponse =
        util.parseClassFromJSON("expected/vendor/delete-response.json", VendorResponse.class);

    String res =
        util.callRestPayload(
            "http://localhost:8080/vendor/vendorId-002/NEXTUPLE_GR",
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
  @Order(6)
  @DisplayName("Happy Path: Get Vendor Details of a Deleted Vendor")
  void getVendorDetailsWithInvalidInput() throws IOException {
    String response =
        util.callRestPayload(
            "http://localhost:8080/vendor/vendorId-002/NEXTUPLE_GR",
            HttpMethod.GET,
            null,
            "message",
            404);
    Assertions.assertEquals("\"Vendor not found with given details\"", response);
  }
}
