package com.nextuple.pe.masterdata.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceRequest;
import com.nextuple.pe.masterdata.domain.inbound.CarrierServiceUpdateRequest;
import com.nextuple.pe.masterdata.domain.outbound.CarrierServiceResponse;
import com.nextuple.pe.masterdata.exception.CarrierServiceDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.CarrierServiceService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrierService")
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

  @GetMapping("/{carrierId}/{serviceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> getCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String serviceId,
      @NotBlank @PathVariable String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing get CarrierService details");
    try {

      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.getCarrierServiceDetails(carrierId, serviceId, orgId);

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

  @PutMapping("/{carrierId}/{serviceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> updateCarrierServiceDetails(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String serviceId,
      @NotBlank @PathVariable String orgId,
      @Valid @RequestBody CarrierServiceUpdateRequest carrierServiceUpdateRequest)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing update CarrierService details");
    try {

      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.updateCarrierServiceDetails(
              carrierId, serviceId, orgId, carrierServiceUpdateRequest);

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

  @DeleteMapping("/{carrierId}/{serviceId}/{orgId}")
  public ResponseEntity<BaseResponse<CarrierServiceResponse>> deleteCarrierService(
      @NotBlank @PathVariable String carrierId,
      @NotBlank @PathVariable String serviceId,
      @NotBlank @PathVariable String orgId)
      throws CarrierServiceDomainException, CommonServiceException {
    logger.info("Processing delete CarrierService");
    try {
      CarrierServiceResponse carrierServiceResponse =
          carrierserviceService.deleteCarrierService(carrierId, serviceId, orgId);

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
