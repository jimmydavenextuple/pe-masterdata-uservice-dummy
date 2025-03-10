package com.nextuple.pe.webhook.util;

import com.nextuple.calendar.consumer.dto.CalendarFeedDto;
import com.nextuple.calendar.consumer.dto.CarrierServiceCalendarFeedDto;
import com.nextuple.calendar.consumer.dto.NodeCalendarFeedDto;
import com.nextuple.calendar.consumer.dto.PickupCalendarFeedDto;
import com.nextuple.calendar.domain.pojo.ExceptionDays;
import com.nextuple.carrier.consumer.dto.CarrierFeedDto;
import com.nextuple.master.data.integration.dto.ResponseDto;
import com.nextuple.master.data.integration.enums.ActionEnum;
import com.nextuple.master.data.integration.inbound.BatchRequest;
import com.nextuple.master.data.integration.outbound.BatchResponse;
import com.nextuple.node.carrier.consumer.dto.NodeCarrierFeedDto;
import com.nextuple.node.carrier.consumer.dto.NodeServiceOptionBufferFeedDto;
import com.nextuple.node.carrier.consumer.dto.ProcessingLeadTimeFeedDto;
import com.nextuple.node.consumer.dto.NodeFeedDto;
import com.nextuple.pe.webhook.domain.dtos.InventoryATP;
import com.nextuple.pe.webhook.domain.dtos.Item;
import com.nextuple.pe.webhook.domain.dtos.MasterDataIngestionDto;
import com.nextuple.pe.webhook.domain.dtos.WebhookConfigDetails;
import com.nextuple.pe.webhook.domain.dtos.WebhookDetail;
import com.nextuple.pe.webhook.domain.inbound.FeedRequest;
import com.nextuple.transit.consumer.dto.TransferScheduleDto;
import com.nextuple.transit.consumer.dto.TransitBufferFeedDto;
import com.nextuple.transit.consumer.dto.TransitFeedDto;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtil {
  public static final String ORG_ID = "NEXTUPLE_GR";
  public static final String IV_RESPONSE_MESSAGE = "Inventory feed published successfully";
  public static final String ITEM_RESPONSE_MESSAGE = "Item feed published successfully";
  public static final String IV_TOPIC = "INTERNAL.TEST.IV";
  public static final String ITEM_TOPIC = "INTERNAL.TEST.ITEM";
  public static final String NODE_TOPIC = "INTERNAL.TEST.PE.NODE_INTEGRATION";
  public static final String CARRIER_TOPIC = "INTERNAL.TEST.PE.CARRIER_INTEGRATION";
  public static final String CALENDAR_TOPIC = "INTERNAL.DEV.PE.CALENDAR_INTEGRATION";
  public static final String CARRIER_SERVICE_CALENDAR_TOPIC =
      "INTERNAL.DEV.PE.CARRIER_SERVICE_CALENDAR_INTEGRATION";
  public static final String NODE_CALENDAR_TOPIC = "INTERNAL.DEV.PE.NODE_CALENDAR_INTEGRATION";
  public static final String PICKUP_CALENDAR_TOPIC = "INTERNAL.DEV.PE.PICKUP_CALENDAR_INTEGRATION";
  public static final String TRANSIT_TOPIC = "INTERNAL.DEV.PE.TRANSIT_INTEGRATION";
  public static final String TRANSIT_BUFFER_TOPIC = "INTERNAL.DEV.PE.TRANSIT_BUFFER_INTEGRATION";
  public static final String WEBHOOK_NAME = "TEST_WEBHOOK";
  public static final String ITEM_HANDLING_TYPE = "conveyable";
  public static final String PROCESSING_LEAD_TIME_TOPIC =
      "INTERNAL.TEST.PE.PROCESSING_LEAD_TIME_INTEGRATION";
  public static final String NODE_SERVICE_OPTION_BUFFER_TOPIC =
      "INTERNAL.TEST.PE.NODE_SERVICE_OPTION_BUFFER_INTEGRATION";
  public static final String NODE_CARRIER_TOPIC = "INTERNAL.TEST.PE.NODE_CARRIER_INTEGRATION";

  public static List<InventoryATP> createInventoryATPs(int size) {
    int n = 0;
    List<InventoryATP> inventoryATPS = new ArrayList<>();
    while (n++ < size) {
      inventoryATPS.add(
          InventoryATP.builder()
              .itemId("item" + n)
              .nodeId("node" + n)
              .unitOfMeasure("in")
              .onhandAvailableQuantity(10)
              .inventoryClass("iv-class" + n)
              .computeTs(10)
              .deliveryMethod("PICK")
              .eventTime(10)
              .futureAvailableQuantity(10)
              .build());
    }
    return inventoryATPS;
  }

  public static List<Item> createItems(int size) {
    int n = 0;
    List<Item> items = new ArrayList<>();
    Map<String, Boolean> serviceOptionEligibilities = new HashMap<>();
    Map<String, List<String>> inventoryNodeTypes = new HashMap<>();
    List<String> inventoryType = new ArrayList<>();
    inventoryType.add("STRING");
    serviceOptionEligibilities.put("sdndEligible", true);
    inventoryNodeTypes.put("sdndEligible", inventoryType);
    while (n++ < size) {
      items.add(
          Item.builder()
              .itemId("item" + n)
              .itemSource("NXT")
              .uom("in")
              .shipEligible(true)
              .parcelShipmentEligible(true)
              .pickEligible(true)
              .serviceOptionEligibilities(serviceOptionEligibilities)
              .inventoryNodeTypes(inventoryNodeTypes)
              .isWhiteGlove(true)
              .handlingType(ITEM_HANDLING_TYPE)
              .buyingCost(20)
              .build());
    }
    return items;
  }

  public static WebhookConfigDetails createWebhookConfigDetails() {
    return WebhookConfigDetails.builder()
        .tenantId(ORG_ID)
        .webhookName(WEBHOOK_NAME)
        .moduleName("TEST_PROMISE")
        .webhookDetail(
            WebhookDetail.builder()
                .endpoint("https://test.pewebhook.com/pe-response")
                .method("POST")
                .headers(
                    Map.of("contentType", "application/json", "authorization", "Basic AUTH-TEST-1"))
                .build())
        .build();
  }

  public ResponseDto createResponseDto(int recordNo, int statusCode, String message) {
    return ResponseDto.builder().recordNo(recordNo).statusCode(statusCode).message(message).build();
  }

  public BatchResponse getBatchResponse(
      int totalRecords, int successfulRecords, int failedRecords) {
    return BatchResponse.builder()
        .totalRecords(totalRecords)
        .successfulRecords(successfulRecords)
        .failedRecords(failedRecords)
        .build();
  }

  public BatchRequest<NodeFeedDto> getNodeFeedRequest(ActionEnum action) {
    BatchRequest<NodeFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeFeedDto());
    return batchRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getNodeFeedIngestionRequest(ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<NodeFeedDto> nodeIngestionRequest = new MasterDataIngestionDto<>();
    nodeIngestionRequest.setAction(action);
    nodeIngestionRequest.setPayload(createNodeFeedDto());
    feedRequest.setData(List.of(nodeIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getCarrierFeedIngestionRequest(ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CarrierFeedDto> carrierIngestionRequest = new MasterDataIngestionDto<>();
    carrierIngestionRequest.setAction(action);
    carrierIngestionRequest.setPayload(createCarrierFeedDto());
    feedRequest.setData(List.of(carrierIngestionRequest));
    return feedRequest;
  }

  public NodeFeedDto createNodeFeedDto() {
    return NodeFeedDto.builder()
        .nodeId("Node-1")
        .nodeType("Store")
        .country("US")
        .isActive(Boolean.TRUE)
        .build();
  }

  public CarrierFeedDto createCarrierFeedDto() {
    return CarrierFeedDto.builder()
        .carrierId("carrier-1")
        .carrierServiceId("carrier-service-1")
        .serviceOptions("SDND")
        .build();
  }

  public BatchRequest<CarrierFeedDto> getCarrierFeedRequest(ActionEnum action) {
    BatchRequest<CarrierFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createCarrierFeedDto());
    return batchRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getCalendarFeedIngestionRequest(ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CalendarFeedDto> calendarIngestionRequest =
        new MasterDataIngestionDto<>();
    calendarIngestionRequest.setAction(action);
    calendarIngestionRequest.setPayload(createCalendarFeedDto());
    feedRequest.setData(List.of(calendarIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getNodeCalendarFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<NodeCalendarFeedDto> nodeCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeCalendarIngestionRequest.setAction(action);
    nodeCalendarIngestionRequest.setPayload(createNodeCalendarFeedDto());
    feedRequest.setData(List.of(nodeCalendarIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getCarrierServiceCalendarFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<CarrierServiceCalendarFeedDto> carrierServiceCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    carrierServiceCalendarIngestionRequest.setAction(action);
    carrierServiceCalendarIngestionRequest.setPayload(createCarrierServiceCalendarFeedDto());
    feedRequest.setData(List.of(carrierServiceCalendarIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getPickupCalendarFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<PickupCalendarFeedDto> pickupCalendarIngestionRequest =
        new MasterDataIngestionDto<>();
    pickupCalendarIngestionRequest.setAction(action);
    pickupCalendarIngestionRequest.setPayload(createPickupCalendarFeedDto());
    feedRequest.setData(List.of(pickupCalendarIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getTransitFeedIngestionRequest(ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<TransitFeedDto> transitIngestionRequest = new MasterDataIngestionDto<>();
    transitIngestionRequest.setAction(action);
    transitIngestionRequest.setPayload(createTransitFeedDto());
    feedRequest.setData(List.of(transitIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getTransitBufferFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<TransitBufferFeedDto> transitBufferIngestionRequest =
        new MasterDataIngestionDto<>();
    transitBufferIngestionRequest.setAction(action);
    transitBufferIngestionRequest.setPayload(createTransitBufferFeedDto());
    feedRequest.setData(List.of(transitBufferIngestionRequest));
    return feedRequest;
  }

  public CalendarFeedDto createCalendarFeedDto() {
    ExceptionDays exceptionDays = new ExceptionDays();
    return CalendarFeedDto.builder()
        .orgId("org-id")
        .calendarId("calendar-id-1")
        .description("description-1")
        .exceptionDays(List.of(exceptionDays))
        .isMondayWorking(true)
        .isTuesdayWorking(true)
        .isWednesdayWorking(true)
        .isThursdayWorking(true)
        .isFridayWorking(true)
        .isSaturdayWorking(false)
        .isSundayWorking(false)
        .build();
  }

  public TransferScheduleDto createTransferScheduleDto() {
    return TransferScheduleDto.builder()
        .orgId("org-id").dropoffNodeId("drop-off-node-id-1").sourceNodeId("source-node-id-1").startTime(new DateTime()).endTime(new DateTime().plusHours(3)).build();
  }

  public NodeCalendarFeedDto createNodeCalendarFeedDto() {
    return NodeCalendarFeedDto.builder()
        .orgId("org-id")
        .calendarId("calendar-id-1")
        .description("description-1")
        .nodeId("node-id-1")
        .effectiveDate("10-02-2024")
        .build();
  }

  public CarrierServiceCalendarFeedDto createCarrierServiceCalendarFeedDto() {
    return CarrierServiceCalendarFeedDto.builder()
        .orgId("org-id")
        .carrierServiceId("carrier-service-1")
        .calendarId("calendar-id-1")
        .shippingStage("shipping-stage")
        .effectiveDate("10-02-2024")
        .build();
  }

  public PickupCalendarFeedDto createPickupCalendarFeedDto() {
    return PickupCalendarFeedDto.builder()
        .orgId("org-id")
        .carrierServiceId("carrier-service-1")
        .calendarId("calendar-id-1")
        .description("description-1")
        .effectiveDate("10-02-2024")
        .nodeId("node-id-1")
        .build();
  }

  public TransitFeedDto createTransitFeedDto() {
    return TransitFeedDto.builder()
        .orgId("org-id")
        .carrierServiceId("carrier-service-1")
        .sourceGeozone("source-geozone")
        .destinationGeozone("destination-geozone")
        .transitDays(0.1f)
        .build();
  }

  public TransitBufferFeedDto createTransitBufferFeedDto() {
    return TransitBufferFeedDto.builder()
        .orgId("org-id")
        .carrierServiceId("carrier-service-1")
        .sourceGeozone("source-geozone")
        .destinationGeozone("destination-geozone")
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public BatchRequest<CalendarFeedDto> getCalendarFeedRequest(ActionEnum action) {
    BatchRequest<CalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<NodeCalendarFeedDto> getNodeCalendarFeedRequest(ActionEnum action) {
    BatchRequest<NodeCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<CarrierServiceCalendarFeedDto> getCarrierServiceCalendarFeedRequest(
      ActionEnum action) {
    BatchRequest<CarrierServiceCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createCarrierServiceCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<PickupCalendarFeedDto> getPickupCalendarFeedRequest(ActionEnum action) {
    BatchRequest<PickupCalendarFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createPickupCalendarFeedDto());
    return batchRequest;
  }

  public BatchRequest<TransitFeedDto> getTransitFeedRequest(ActionEnum action) {
    BatchRequest<TransitFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransitFeedDto());
    return batchRequest;
  }

  public BatchRequest<TransitBufferFeedDto> getTransitBufferFeedRequest(ActionEnum action) {
    BatchRequest<TransitBufferFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransitBufferFeedDto());
    return batchRequest;
  }

  public BatchRequest<NodeCarrierFeedDto> getNodeCarrierFeedRequest(ActionEnum action) {
    BatchRequest<NodeCarrierFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeCarrierFeedDto());
    return batchRequest;
  }

  public BatchRequest<ProcessingLeadTimeFeedDto> getProcessingLeadTimeFeedRequest(
      ActionEnum action) {
    BatchRequest<ProcessingLeadTimeFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createProcessingLeadTimeFeedDto());
    return batchRequest;
  }

  public BatchRequest<NodeServiceOptionBufferFeedDto> getNodeServiceOptionBufferFeedRequest(
      ActionEnum action) {
    BatchRequest<NodeServiceOptionBufferFeedDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createNodeServiceOptionBufferFeedDto());
    return batchRequest;
  }

  public NodeCarrierFeedDto createNodeCarrierFeedDto() {
    return NodeCarrierFeedDto.builder()
        .orgId(ORG_ID)
        .nodeId("node-1")
        .carrierServiceId("carrier-service-1")
        .serviceOption("SDND")
        .lastPickupTime("23:00")
        .build();
  }

  public ProcessingLeadTimeFeedDto createProcessingLeadTimeFeedDto() {
    return ProcessingLeadTimeFeedDto.builder()
        .orgId(ORG_ID)
        .nodeId("node-1")
        .serviceOption("SDND")
        .processingTime(2.0)
        .build();
  }

  public NodeServiceOptionBufferFeedDto createNodeServiceOptionBufferFeedDto() {
    return NodeServiceOptionBufferFeedDto.builder()
        .orgId(ORG_ID)
        .nodeId("node-1")
        .serviceOption("SDND")
        .bufferHours(20.0)
        .bufferStartDate(new Date())
        .bufferEndDate(new Date())
        .build();
  }

  public FeedRequest<MasterDataIngestionDto<?>> getNodeCarrierFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<NodeCarrierFeedDto> nodeCarrierIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeCarrierIngestionRequest.setAction(action);
    nodeCarrierIngestionRequest.setPayload(createNodeCarrierFeedDto());
    feedRequest.setData(List.of(nodeCarrierIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getNodeServiceOptionFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<ProcessingLeadTimeFeedDto> nodeServiceOptionIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeServiceOptionIngestionRequest.setAction(action);
    nodeServiceOptionIngestionRequest.setPayload(createProcessingLeadTimeFeedDto());
    feedRequest.setData(List.of(nodeServiceOptionIngestionRequest));
    return feedRequest;
  }

  public FeedRequest<MasterDataIngestionDto<?>> getNodeServiceOptionBufferFeedIngestionRequest(
      ActionEnum action) {
    FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
    MasterDataIngestionDto<NodeServiceOptionBufferFeedDto> nodeServiceOptionBufferIngestionRequest =
        new MasterDataIngestionDto<>();
    nodeServiceOptionBufferIngestionRequest.setAction(action);
    nodeServiceOptionBufferIngestionRequest.setPayload(createNodeServiceOptionBufferFeedDto());
    feedRequest.setData(List.of(nodeServiceOptionBufferIngestionRequest));
    return feedRequest;
  }

    public FeedRequest<MasterDataIngestionDto<?>> getTransferScheduleFeedIngestionRequest(ActionEnum action) {
      FeedRequest<MasterDataIngestionDto<?>> feedRequest = new FeedRequest<>();
      MasterDataIngestionDto<TransferScheduleDto> transferScheduleIngestionRequest =
              new MasterDataIngestionDto<>();
      transferScheduleIngestionRequest.setAction(action);
      transferScheduleIngestionRequest.setPayload(createTransferScheduleDto());
      feedRequest.setData(List.of(transferScheduleIngestionRequest));
      return feedRequest;
    }

  public BatchRequest<TransferScheduleDto> getTransferScheduleFeedRequest(ActionEnum action) {
    BatchRequest<TransferScheduleDto> batchRequest = new BatchRequest<>();
    batchRequest.setAction(action);
    batchRequest.setRecordNo(1);
    batchRequest.setPayload(createTransferScheduleDto());
    return batchRequest;
  }


}
