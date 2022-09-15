package com.hbc.pe.masterdata.calendar.controller;

import com.hbc.calendar.domain.dto.NodeCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.NodeCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCalendarResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.service.NodeCalendarService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/node-calendar")
@RequiredArgsConstructor
public class NodeCalendarController {

  private static final Logger logger = LoggerFactory.getLogger(NodeCalendarController.class);
  private final NodeCalendarService nodeCalendarService;

  @PostMapping
  public ResponseEntity<BaseResponse<NodeCalendarResponse>> handleCreateNodeCalendar(
      @Valid @RequestBody NodeCalendarRequest nodeCalendarRequest)
      throws CalendarDomainException, CommonServiceException, DateException {
    logger.debug(
        "Inside handleCreateNodeCalendar() for nodeCalendarRequest: {}", nodeCalendarRequest);
    try {
      var nodeCalendarResponse = nodeCalendarService.processCreateNodeCalendar(nodeCalendarRequest);
      logger.info("Response after creation of node calendar:{}", nodeCalendarResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Node calendar created successfully!")
              .payload(nodeCalendarResponse)
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

  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<NodeCalendarCacheKeyDto>>> getNodeCalendarCacheKeys(
      @RequestParam Integer limit) throws CalendarDomainException {
    logger.debug("Processing get Node Calendar Cache Keys");

    var response = nodeCalendarService.getAllNodeCalendarCacheKeys(limit);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Node Calendar Cache Keys fetched successfully")
            .payload(response)
            .build());
  }
}
