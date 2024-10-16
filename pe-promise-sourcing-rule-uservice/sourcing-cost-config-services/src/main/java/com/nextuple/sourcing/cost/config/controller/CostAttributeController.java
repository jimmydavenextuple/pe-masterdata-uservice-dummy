/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.sourcing.cost.config.controller.docs.CreateCostAttributeDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeByAttributeNameDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeByCostAttributeIdDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributeCacheKeyDoc;
import com.nextuple.sourcing.cost.config.controller.docs.GetCostAttributesListDoc;
import com.nextuple.sourcing.cost.config.controller.docs.UpdateCostAttributeDoc;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDetailsCacheKeyDto;
import com.nextuple.sourcing.cost.config.dto.CostAttributeDto;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeRequest;
import com.nextuple.sourcing.cost.config.inbound.CostAttributeUpdateRequest;
import com.nextuple.sourcing.cost.config.service.CostAttributeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/cost-attribute")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cost Attribute Details APIs")
public class CostAttributeController {

  private static final Logger logger = LoggerFactory.getLogger(CostAttributeController.class);

  private final CostAttributeService costAttributeService;

  @CreateCostAttributeDoc
  @PostMapping
  public ResponseEntity<BaseResponse<CostAttributeDto>> createCostAttribute(
      @Valid @RequestBody CostAttributeRequest costAttributeRequest) throws CommonServiceException {
    log.debug("Processing create Cost Attribute request :{}", costAttributeRequest);
    var costAttributeDto = costAttributeService.createCostAttribute(costAttributeRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            BaseResponse.builder()
                .message("Cost Attribute created successfully!")
                .payload(costAttributeDto)
                .build());
  }

  @GetCostAttributeByCostAttributeIdDoc
  @GetMapping(value = "/{costAttributeId}")
  public ResponseEntity<BaseResponse<CostAttributeDto>> getCostAttributeDetailsById(
      @NotNull(message = "Unique identifier for cost attribute can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost attribute", example = "1")
          Long costAttributeId)
      throws CommonServiceException {
    log.debug("Processing get Cost Attribute details by id :{} request", costAttributeId);
    var costAttributeDto = costAttributeService.findCostAttributeDetailsById(costAttributeId);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute details fetched successfully by id!")
            .payload(costAttributeDto)
            .build());
  }

  @UpdateCostAttributeDoc
  @PutMapping(value = "/{costAttributeId}")
  public ResponseEntity<BaseResponse<CostAttributeDto>> updateCostAttribute(
      @NotNull(message = "Unique identifier for cost attribute can't be null")
          @PathVariable
          @Parameter(description = "Unique identifier for cost attribute", example = "1")
          Long costAttributeId,
      @Valid @RequestBody CostAttributeUpdateRequest costAttributeUpdateRequest)
      throws CommonServiceException {
    log.debug("Processing update Cost Attribute request :{}", costAttributeUpdateRequest);
    var costAttributeDto =
        costAttributeService.updateCostAttribute(costAttributeId, costAttributeUpdateRequest);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute updated successfully!")
            .payload(costAttributeDto)
            .build());
  }

  @GetCostAttributeByAttributeNameDoc
  @GetMapping(value = "/attribute-name/{attributeName}")
  public ResponseEntity<BaseResponse<CostAttributeDto>> getCostAttributeDetailsByAttributeName(
      @NotBlank(message = "Name of the attribute can't be blank")
          @PathVariable
          @Parameter(description = "Name of the attribute", example = "1")
          String attributeName)
      throws CommonServiceException {
    log.debug("Processing get Cost Attribute details by attributeName :{} request", attributeName);
    var costAttributeDto =
        costAttributeService.findCostAttributeDetailsByAttributeName(attributeName);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute details fetched successfully by attribute name!")
            .payload(costAttributeDto)
            .build());
  }

  @GetCostAttributesListDoc
  @GetMapping(value = "/list")
  public ResponseEntity<BaseResponse<List<CostAttributeDto>>> getCostAttributeDetailsList() {
    log.debug("Processing get Cost Attribute details list request");
    var costAttributeDto = costAttributeService.findCostAttributeDetailsList();
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attribute list fetched successfully!")
            .payload(costAttributeDto)
            .build());
  }

  /**
   * Delete all cost attributes.
   *
   * @deprecated Not for public use. This API is just used for automation test cases.
   */
  @Deprecated(forRemoval = false)
  @DeleteMapping(value = "/delete-all")
  public ResponseEntity<BaseResponse<List<CostAttributeDto>>> deleteCostAttributeDetails() {
    log.debug("Processing delete Cost Attribute details list request");
    var costAttributeDtoList = costAttributeService.deleteCostAttributeDetails();
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost Attributes deleted successfully!")
            .payload(costAttributeDtoList)
            .build());
  }

  @GetCostAttributeCacheKeyDoc
  @GetMapping("/get-all-cache-keys")
  public ResponseEntity<BaseResponse<List<CostAttributeDetailsCacheKeyDto>>>
      getCostAttributeDetailsCacheKeys(@RequestParam(defaultValue = "100") Integer limit) {
    logger.debug("Processing cost attribute details Cache Keys");
    var response = costAttributeService.getAllCostAttributeCacheKeys(limit);
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Cost attribute details Keys fetched successfully")
            .payload(response)
            .build());
  }
}
