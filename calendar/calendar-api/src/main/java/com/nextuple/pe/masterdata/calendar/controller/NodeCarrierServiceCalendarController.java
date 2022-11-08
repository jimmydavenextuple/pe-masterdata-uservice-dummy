package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.exception.DateException;
import com.nextuple.pe.masterdata.calendar.service.NodeCarrierServiceCalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/node-carrier-service-calendar")
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarController.class);
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierServiceCalendarResponse>>
      handleCreateNodeCarrierServiceCalendar(
          @Valid @RequestBody NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
          throws CalendarDomainException, CommonServiceException, DateException {
    logger.debug(
        "Inside handleCreateNodeCarrierServiceCalendar() for nodeCarrierServiceCalendarRequest: {}",
        nodeCarrierServiceCalendarRequest);
    try {
      var nodeCarrierServiceCalendarResponse =
          nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
              nodeCarrierServiceCalendarRequest);
      logger.info(
          "Response after creation of node carrier service calendar:{}",
          nodeCarrierServiceCalendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node carrier service calendar created successfully!")
              .payload(nodeCarrierServiceCalendarResponse)
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateNodeCarrierServiceCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{nodeId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>>
      handleGetNodeCarrierServiceCalendar(
          @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
          @NotBlank(message = "orgId can't be empty") @PathVariable String nodeId,
          @NotBlank(message = "orgId can't be empty") @PathVariable String carrierServiceId,
          @RequestParam Optional<String> serviceOption)
          throws CalendarDomainException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node carrier&Service calendar details fetched successfully!")
              .payload(
                  nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
                      orgId, nodeId, carrierServiceId, serviceOption))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetNodeCarrierServiceCalendar()");
      throw e;
    }
  }

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCarrierCalendarCacheKeyDto>>>
      getNodeCarrierCalendarCacheKeys(@RequestParam Integer limit) throws CalendarDomainException {
    logger.debug("Processing get Node Carrier Calendar Cache Keys");

    var response = nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Carrier Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
