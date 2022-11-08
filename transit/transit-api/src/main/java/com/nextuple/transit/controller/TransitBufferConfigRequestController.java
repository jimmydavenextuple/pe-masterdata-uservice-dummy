package com.nextuple.transit.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferConfigRequestService;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>>
      processTransitBufferConfigRequest(
          @Valid @RequestBody TransitBufferConfigRequest transitBufferConfigRequest)
          throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
              CsvException {
    logger.debug("Processing transit buffer config creation request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.processTransitBufferRequest(transitBufferConfigRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config request successfully created")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process transit buffer config request");
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

  @DeleteMapping
  public ResponseEntity<BaseResponse<TransitBufferConfigResponse>> deleteTransitBufferConfigRequest(
      @NotNull(message = "transitBufferRequestId can't be null") @RequestParam
          Long transitBufferRequestId,
      @NotBlank(message = "createdBy can't be blank") @RequestParam String createdBy)
      throws CommonServiceException, IOException, TransitBufferReqJobRefDomainException,
          CsvException {
    logger.debug("Processing transit buffer config creation request");
    try {
      var transitBufferConfigResponse =
          transitBufferConfigRequestService.deleteTransitBufferRequest(
              transitBufferRequestId, createdBy);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer config request successfully deleted")
              .payload(transitBufferConfigResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete transit buffer config request");
      throw e;
    }
  }
}
