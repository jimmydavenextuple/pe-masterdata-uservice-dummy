package com.nextuple.pe.masterdata.calendar.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.calendar.domain.inbound.NodeCalendarRequest;
import com.nextuple.pe.masterdata.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.service.NodeCalendarService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nodeCalendar")
@RequiredArgsConstructor
public class NodeCalendarController {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarController.class);
  private final NodeCalendarService nodeCalendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeCalendarResponse>> handleCreateNodeCalendar(
      @Valid @RequestBody NodeCalendarRequest nodeCalendarRequest) throws CalendarDomainException {
    logger.info(
        "Inside handleCreateNodeCalendar() for nodeCalendarRequest: {}", nodeCalendarRequest);
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node calendar created successfully!")
              .payload(nodeCalendarService.processCreateNodeCalendar(nodeCalendarRequest))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleCreateNodeCalendar()");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{nodeId}")
  public ResponseEntity<BaseResponse<List<NodeCalendarResponse>>> handleGetNodeCalendar(
      @PathVariable String orgId, @PathVariable String nodeId) throws CalendarDomainException {
    try {
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node calendar details fetched successfully!")
              .payload(nodeCalendarService.processGetNodeCalendar(orgId, nodeId))
              .build());
    } catch (Exception e) {
      logger.error("Error in handleGetNodeCalendar()");
      throw e;
    }
  }
}
