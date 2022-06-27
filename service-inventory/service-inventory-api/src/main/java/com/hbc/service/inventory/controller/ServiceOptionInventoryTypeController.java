package com.hbc.service.inventory.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.service.inventory.domain.inbound.ServiceInventoryRequest;
import com.hbc.service.inventory.domain.outbound.ServiceInventoryDto;
import com.hbc.service.inventory.exception.CommonServiceException;
import com.hbc.service.inventory.exception.ServiceInventoryDomainException;
import com.hbc.service.inventory.service.ServiceOptionInventoryTypeService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
