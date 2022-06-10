package com.nextuple.pe.masterdata.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.pe.masterdata.domain.inbound.ServiceInventoryRequest;
import com.nextuple.pe.masterdata.domain.outbound.ServiceInventoryDto;
import com.nextuple.pe.masterdata.exception.ServiceInventoryDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import com.nextuple.pe.masterdata.service.ServiceOptionInventoryTypeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/serviceOption/inventoryType")
@RequiredArgsConstructor
public class ServiceOptionInventoryTypeController {
  private static final Logger logger =
      LoggerFactory.getLogger(ServiceOptionInventoryTypeController.class);
  private final ServiceOptionInventoryTypeService serviceOptionInventoryTypeService;

  @PostMapping
  public ResponseEntity<BaseResponse<ServiceInventoryDto>> createServiceOptionInventoryType(
      @Valid @RequestBody ServiceInventoryRequest serviceToInventoryRequest)
      throws ServiceInventoryDomainException {
    logger.info("Processing ServiceOptionInventoryTypeMapping creation request");
    try {
      ServiceInventoryDto serviceToInventoryDto =
          serviceOptionInventoryTypeService.createServiceOptionInventoryType(
              serviceToInventoryRequest);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("ServiceOptionInventoryTypeMapping successfully created")
              .payload(serviceToInventoryDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create ServiceOptionInventoryTypeMapping");
      throw e;
    }
  }

  @GetMapping("/{orgId}/{serviceOption}")
  public ResponseEntity<BaseResponse<ServiceInventoryDto>> getServiceOptionToInventoryMapping(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String serviceOption)
      throws ServiceInventoryDomainException, CommonServiceException {
    logger.info("Processing get ServiceOptionToInventoryMapping details");
    try {

      ServiceInventoryDto serviceToInventoryDto =
          serviceOptionInventoryTypeService.getServiceOptionInventoryTypeMapping(
              orgId, serviceOption);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("ServiceOptionToInventoryMapping fetched successfully")
              .payload(serviceToInventoryDto)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch ServiceOptionToInventoryMapping");
      throw e;
    }
  }
}
