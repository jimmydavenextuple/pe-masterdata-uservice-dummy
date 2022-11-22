package com.hbc.transit.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.transit.domain.inbound.TransitBufferRequest;
import com.hbc.transit.domain.outbound.TransitBufferResponse;
import com.hbc.transit.service.TransitBufferService;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

@RestController
@RequestMapping("/transit/v1/buffer")
@RequiredArgsConstructor
@Validated
public class TransitBufferController {

  private final TransitBufferService transitBufferService;

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferController.class);

  @PostMapping
  public ResponseEntity<BaseResponse<TransitBufferResponse>> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.saveTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer created successfully")
            .payload(response)
            .build());
  }

  @GetMapping("/org/{orgId}")
  public ResponseEntity<BaseResponse<List<TransitBufferResponse>>> getByOrgIdAndDestinationGeozone(
      @PathVariable String orgId,
      @NotBlank(message = "destinationGeozone can't be blank") @RequestParam
          String destinationGeozone)
      throws CommonServiceException {
    logger.debug("Processing get all transit buffers by orgId and destination geozone");
    var response =
        transitBufferService.getTransitBuffersByOrgIdAndDestinationGeozone(
            orgId, destinationGeozone);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details fetched successfully")
            .payload(response)
            .build());
  }

  @PutMapping
  public ResponseEntity<BaseResponse<TransitBufferResponse>> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing transit buffer creation request");
    var response = transitBufferService.updateTransitBuffer(transitBufferRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer updated successfully")
            .payload(response)
            .build());
  }

  @DeleteMapping
  public ResponseEntity<BaseResponse<TransitBufferResponse>> deleteTransitBufferDetails(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest) throws CommonServiceException {
    logger.debug("Processing delete transit buffer details");

    var response =
        transitBufferService.deleteTransitBufferDetails(
            transitBufferRequest.getOrgId(),
            transitBufferRequest.getCarrierServiceId(),
            transitBufferRequest.getSourceGeozone(),
            transitBufferRequest.getDestinationGeozone());

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer deleted successfully")
            .payload(response)
            .build());
  }

  @GetMapping("/{transitBufferConfigRequestId}")
  public ResponseEntity<BaseResponse<PreSignedUrlResponse>> getTransitBufferDetails(
      @PathVariable Long transitBufferConfigRequestId,
      @NotBlank(message = "createdBy can't be empty") @RequestParam String createdBy)
      throws IOException, CommonServiceException {
    logger.debug("Processing get transit buffer details by transitBufferRequestId");

    var response =
        transitBufferService.getTransitBufferDetails(transitBufferConfigRequestId, createdBy);

    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit buffer details with pre signed url fetched successfully")
            .payload(response)
            .build());
  }
}
