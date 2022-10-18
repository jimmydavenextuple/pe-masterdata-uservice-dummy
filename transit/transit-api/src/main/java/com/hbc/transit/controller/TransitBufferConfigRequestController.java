package com.hbc.transit.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.hbc.transit.domain.inbound.TransitBufferConfigRequest;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import com.hbc.transit.service.TransitBufferConfigRequestService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/transit/buffer-config-request")
@RequiredArgsConstructor
public class TransitBufferConfigRequestController {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferConfigRequestController.class);
  private final TransitBufferConfigRequestService transitBufferConfigRequestService;

  @PostMapping()
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> createTransitBufferConfigRequest(
      @Valid @RequestBody TransitBufferConfigRequest transitBufferConfigRequest)
      throws CommonServiceException {
    logger.debug("Processing transit buffer config creation request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.createTransitBufferRequest(transitBufferConfigRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config request successfully created")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create transit buffer config request");
      throw e;
    }
  }

  @PutMapping("/update-status/{id}")
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>>
      updateTransitBufferConfigRequestStatus(
          @PathVariable Long id,
          @RequestParam @NotNull @Valid TransitBufferConfigRequestStatusEnum status)
          throws CommonServiceException {
    logger.debug("Processing transit buffer config update status request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.updateTransitBufferRequestStatus(id, status);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config status updation request successfully processed")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process transit buffer config status updation request");
      throw e;
    }
  }

  @GetMapping("/{orgId}")
  public ResponseEntity<BaseResponse<List<TransitBufferConfigResponse>>>
      getTransitBufferConfigRequests(
          @PathVariable String orgId,
          @NotBlank(message = "carrierServiceId can't be blank") @RequestParam
              String carrierServiceId)
          throws CommonServiceException {
    logger.debug("Processing get transit buffer config requests");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.fetchTransitBufferRequests(orgId, carrierServiceId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config requests successfully fetched")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get transit buffer config requests");
      throw e;
    }
  }
}
