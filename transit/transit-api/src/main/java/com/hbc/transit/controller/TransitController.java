package com.hbc.transit.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.inbound.TransitBufferCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.inbound.TransitDetailsRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.hbc.transit.exception.TransitDomainException;
import com.hbc.transit.service.TransitService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
@RequestMapping("/transit")
@RequiredArgsConstructor
public class TransitController {

  private static final Logger logger = LoggerFactory.getLogger(TransitController.class);
  private final TransitService transitService;

  @PostMapping
  public ResponseEntity<BaseResponse<TransitResponse>> addTransitData(
      @Valid @RequestBody TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing transit data creation request");
    try {
      var transitResponse = transitService.addTransitInfo(transitDataCreationRequest);
      logger.info("Response after addition of transit data :{}", transitResponse);
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

  @PutMapping("/buffer")
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitBufferDetails(
      @Valid @RequestBody TransitBufferCreationRequest transitBufferCreationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing update transit buffer data");
    try {
      var transitResponse = transitService.updateTransitBufferDetails(transitBufferCreationRequest);
      logger.info("Response after updation of transit buffer details :{}", transitResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit details updated successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update transit buffer details");
      throw e;
    }
  }

  @PutMapping("/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitData(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "sourceGeozone can't be empty") @PathVariable String sourceGeozone,
      @NotBlank(message = "destinationGeozone can't be empty") @PathVariable
          String destinationGeozone,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @Valid @RequestBody TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing update transit data");
    try {

      var transitResponse =
          transitService.updateTransitDetails(
              orgId,
              sourceGeozone,
              destinationGeozone,
              carrierServiceId,
              transitDataUpdationRequest);
      logger.info("Response after updation of transit data :{}", transitResponse);

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
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "sourceGeozone can't be empty") @PathVariable String sourceGeozone,
      @NotBlank(message = "destinationGeozone can't be empty") @PathVariable
          String destinationGeozone,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String serviceOption)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing get transit details");
    try {

      var transitResponse =
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
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "sourceGeozone can't be empty") @PathVariable String sourceGeozone,
      @NotBlank(message = "destinationGeozone can't be empty") @PathVariable
          String destinationGeozone,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing delete transit details");
    try {

      var transitResponse =
          transitService.deleteTransitDetails(
              orgId, sourceGeozone, destinationGeozone, carrierServiceId);
      logger.info("Response after deletion of transit data :{}", transitResponse);

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

  @GetMapping("/{orgId}/{destinationGeozone}")
  public ResponseEntity<BaseResponse<List<TransitResponse>>> getTransitDetailsList(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "destinationGeozone can't be empty") @PathVariable
          String destinationGeozone,
      @NotNull @RequestParam List<String> sourceGeozones)
      throws TransitDomainException {
    logger.debug("Processing get transit details list");
    try {

      var transitResponse =
          transitService.getListOfTransitDetails(orgId, destinationGeozone, sourceGeozones);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("List of transit details fetched successfully")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch transit details list");
      throw e;
    }
  }

  @GetMapping("distinct/dFSA/{orgId}/{sourceGeozone}")
  public ResponseEntity<BaseResponse<List<String>>> getDistinctDestinationFSAList(
      @NotBlank(message = "orgId can't be blank") @PathVariable String orgId,
      @NotBlank(message = "sourceGeozone can't be empty") @PathVariable String sourceGeozone,
      @NotEmpty(message = "carrier service id list can't be empty") @RequestBody
          List<String> carrierServiceIds)
      throws TransitDomainException {
    logger.debug("Processing distinct destination geozone request");
    try {
      var transitResponse = transitService.getDistinctDFSA(orgId, sourceGeozone, carrierServiceIds);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Fetched distinct list of destination geozones")
              .payload(transitResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch distinct destination geozones");
      throw e;
    }
  }

  @GetMapping("/transit-entries/{orgId}/{carrierServiceId}")
  public ResponseEntity<BaseResponse<TransitTimeEntriesDto>> getTransitTimeEntries(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "carrierServiceId can't be empty") @PathVariable String carrierServiceId)
      throws TransitDomainException {
    logger.debug("Processing get transit time entries");
    var transitTimeEntriesDto = transitService.getTransitTimeEntries(orgId, carrierServiceId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time entries fetched successfully")
            .payload(transitTimeEntriesDto)
            .build());
  }

  @GetMapping("/batch-transit/{orgId}/{destinationGeozone}")
  public BaseResponse<List<TransitResponse>> getTransitDetailsListForDestinationGeoZone(
      @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
      @NotBlank(message = "destinationGeozone can't be empty") @PathVariable
          String destinationGeozone)
      throws TransitDomainException, CommonServiceException {
    logger.debug("Processing get transit details list");
    try {
      return BaseResponse.builder()
          .payload(
              transitService.getListOfTransitDetailsForDestinationGeoZone(
                  orgId, destinationGeozone))
          .build();
    } catch (Exception e) {
      logger.error("Failed to fetch transit details list");
      throw e;
    }
  }

  @PostMapping("/transit-entries/{orgId}/{carrierServiceId}/geozones")
  public ResponseEntity<BaseResponse<List<TransitResponse>>>
      getTransitTimeDetailsForDestinationGeoZonesList(
          @NotBlank(message = "orgId can't be empty") @PathVariable String orgId,
          @NotBlank(message = "carrierServiceId can't be empty") @PathVariable
              String carrierServiceId,
          @RequestBody TransitDetailsRequest transitDetailsRequest)
          throws TransitDomainException {
    logger.debug("Processing get transit time entries");
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Transit time entries fetched successfully")
            .payload(
                transitService.getTransitDetailsForDestinationGeozones(
                    orgId, carrierServiceId, transitDetailsRequest.getDestinationGeozones()))
            .build());
  }

  @PutMapping("/{orgId}/{carrierServiceId}/buffer-days")
  public ResponseEntity<BaseResponse<TransitResponse>> updateTransitBufferDays(
      @PathVariable String orgId,
      @PathVariable String carrierServiceId,
      @NotBlank(message = "sourceGeoZone can't be blank") @RequestParam String sourceGeoZone,
      @NotBlank(message = "destinationGeoZone can't be blank") @RequestParam
          String destinationGeoZone)
      throws TransitDomainException {
    logger.debug("Processing delete transit buffer days");
    return ResponseEntity.ok()
        .body(
            BaseResponse.builder()
                .message("Transit buffer days removed successfully")
                .payload(
                    transitService.updateTransitBufferDays(
                        orgId, carrierServiceId, sourceGeoZone, destinationGeoZone))
                .build());
  }
}
