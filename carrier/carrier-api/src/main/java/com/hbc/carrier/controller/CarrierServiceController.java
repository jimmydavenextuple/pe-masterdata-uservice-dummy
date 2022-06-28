package com.hbc.carrier.controller;

import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.service.CarrierServiceService;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
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
@RequestMapping("/carrier-service")
@RequiredArgsConstructor
public class CarrierServiceController {

  private static final Logger logger = LoggerFactory.getLogger(CarrierServiceController.class);
  private final CarrierServiceService carrierserviceService;

  @PostMapping
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> createCarrierService(
      @Valid @RequestBody CarrierServiceRequest carrierServiceRequest)
      throws CarrierServiceDomainException {
    logger.info("Processing CarrierService creation request");
    try {
      System.out.println(carrierServiceRequest);
      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.createCarrierService(carrierServiceRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Carrier Service successfully created")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create CarrierService");
      throw e;
    }
  }

  @GetMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> getCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing get CarrierService details");
    try {

      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.getCarrierServiceDetails(carrierId, carrierServiceId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService details fetched successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch CarrierService details");
      throw e;
    }
  }

  @PutMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> updateCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody CarrierServiceUpdateRequest carrierServiceUpdateRequest)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing update CarrierService details");
    try {

      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.updateCarrierServiceDetails(
              carrierId, carrierServiceId, orgId, carrierServiceUpdateRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService details updated successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update CarrierService details");
      throw e;
    }
  }

  @DeleteMapping("/{carrierId}/{carrierServiceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> deleteCarrierService(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String carrierServiceId,
      @NotBlank @PathVariable String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing delete CarrierService");
    try {
      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.deleteCarrierService(carrierId, carrierServiceId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("CarrierService deleted successfully")
              .payload(carrierServiceResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to ");
      throw e;
    }
  }
}
