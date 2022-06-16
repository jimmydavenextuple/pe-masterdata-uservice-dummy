package com.nextuple.transit.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import com.nextuple.transit.exception.CommonServiceException;
import com.nextuple.transit.exception.TransitDomainException;
import com.nextuple.transit.service.TransitService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transit")
@RequiredArgsConstructor
public class TransitController {

  private static final Logger logger = LoggerFactory.getLogger(TransitController.class);
  private final TransitService transitService;

  @PostMapping
  public ResponseEntity<BaseResponse<TransitResponse>> addTransitData(
      @Valid @RequestBody TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException {
    logger.info("Processing transit data creation request");
    try {
      TransitResponse transitResponse = transitService.addTransitInfo(transitDataCreationRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit data successfully added")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to add transit data");
      throw e;
    }
  }

  @PutMapping("/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitData(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String sourceGeozone,
      @NotBlank @PathVariable String destinationGeozone,
      @NotBlank @PathVariable String carrierServiceId,
      @Valid @RequestBody TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.info("Processing update transit data");
    try {

      TransitResponse transitResponse =
          transitService.updateTransitDetails(
              orgId,
              sourceGeozone,
              destinationGeozone,
              carrierServiceId,
              transitDataUpdationRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details updated successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update transit details");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}/{serviceOption}")
  public ResponseEntity<BaseResponse<TransitResponse>> getTransitDetails(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String sourceGeozone,
      @NotBlank @PathVariable String destinationGeozone,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption)
      throws TransitDomainException, CommonServiceException {
    logger.info("Processing get transit details");
    try {

      TransitResponse transitResponse =
          transitService.getTransitDetails(
              orgId, sourceGeozone, destinationGeozone, carrierServiceId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details fetched successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch transit details");
      throw e;
    }
  }

  @DeleteMapping("/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<TransitResponse>> deleteTransitDetails(
      @NotBlank @PathVariable String orgId,
      @NotBlank @PathVariable String sourceGeozone,
      @NotBlank @PathVariable String destinationGeozone,
      @NotBlank @PathVariable String carrierServiceId)
      throws TransitDomainException, CommonServiceException {
    logger.info("Processing delete transit details");
    try {

      TransitResponse transitResponse =
          transitService.deleteTransitDetails(
              orgId, sourceGeozone, destinationGeozone, carrierServiceId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details deleted successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete transit details");
      throw e;
    }
  }
}
