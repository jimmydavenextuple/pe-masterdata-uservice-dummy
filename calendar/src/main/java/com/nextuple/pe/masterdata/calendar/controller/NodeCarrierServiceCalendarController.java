package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.service.NodeCarrierServiceCalendarService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nodeCarrierServiceCalendar")
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarController {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarController.class);
  private final NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeCarrierServiceCalendarResponse>>
      handleCreateNodeCarrierServiceCalendar(
          @Valid @RequestBody NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
          throws CalendarDomainException {
    logger.info(
        "Inside handleCreateNodeCarrierServiceCalendar() for nodeCarrierServiceCalendarRequest: {}",
        nodeCarrierServiceCalendarRequest);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node carrier service calendar created successfully!")
              .payload(
                  nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                      nodeCarrierServiceCalendarRequest))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateNodeCarrierServiceCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{nodeId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<List<NodeCarrierServiceCalendarResponse>>>
      handleGetNodeCarrierServiceCalendar(
          @PathVariable String orgId,
          @PathVariable String nodeId,
          @PathVariable String carrierServiceId,
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
}
