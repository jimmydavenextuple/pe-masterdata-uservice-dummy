/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postalcodecarrierservice.controller;

import com.nextuple.postalcodecarrierservice.domain.PostalCodeCarrierServiceConstants;
import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceCacheKeyDto;
import com.nextuple.postalcodecarrierservice.domain.dto.PostalCodeCarrierServiceDto;
import com.nextuple.postalcodecarrierservice.domain.inbound.PostalCodeCarrierServiceRequest;
import com.nextuple.postalcodecarrierservice.domain.outbound.PostalCodeCarrierServiceResponse;
import com.nextuple.postalcodecarrierservice.service.PostalCodeCarrierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/postal-code-carrier-service")
@RequiredArgsConstructor
@Slf4j
@Tag(
    name = "Postal Code Carrier Service",
    description = "API for managing postal code carrier service mappings")
public class PostalCodeCarrierServiceController {

  private final PostalCodeCarrierService postalCodeCarrierService;

  @PostMapping
  @Operation(
      summary = "Create Postal Code Carrier Service",
      description = PostalCodeCarrierServiceConstants.CREATE_POSTAL_CODE_CARRIER_SERVICE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.CREATE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS)
  public ResponseEntity<PostalCodeCarrierServiceResponse> createPostalCodeCarrierService(
      @RequestBody PostalCodeCarrierServiceRequest request) {

    log.info(
        "Creating postal code carrier service for zipcode: {} and carrier service: {}",
        request.getZipcode(),
        request.getCarrierServiceId());

    PostalCodeCarrierServiceDto dto =
        postalCodeCarrierService.createPostalCodeCarrierService(request);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder().postalCodeCarrierServices(List.of(dto)).build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{zipcode}/{carrierServiceId}")
  @Operation(
      summary = "Get Postal Code Carrier Service",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_SUCCESS)
  public ResponseEntity<PostalCodeCarrierServiceResponse> getPostalCodeCarrierService(
      @Parameter(
              description = PostalCodeCarrierServiceConstants.ZIP_CODE,
              example = PostalCodeCarrierServiceConstants.ZIP_CODE_EXAMPLE)
          @PathVariable
          String zipcode,
      @Parameter(
              description = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID,
              example = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId) {

    log.info(
        "Getting postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDto dto =
        postalCodeCarrierService.getPostalCodeCarrierService(zipcode, carrierServiceId);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder().postalCodeCarrierServices(List.of(dto)).build();

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{zipcode}/{carrierServiceId}")
  @Operation(
      summary = "Update Postal Code Carrier Service",
      description = PostalCodeCarrierServiceConstants.UPDATE_POSTAL_CODE_CARRIER_SERVICE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.UPDATE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS)
  public ResponseEntity<PostalCodeCarrierServiceResponse> updatePostalCodeCarrierService(
      @Parameter(
              description = PostalCodeCarrierServiceConstants.ZIP_CODE,
              example = PostalCodeCarrierServiceConstants.ZIP_CODE_EXAMPLE)
          @PathVariable
          String zipcode,
      @Parameter(
              description = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID,
              example = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId,
      @RequestBody PostalCodeCarrierServiceRequest request) {

    log.info(
        "Updating postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    PostalCodeCarrierServiceDto dto =
        postalCodeCarrierService.updatePostalCodeCarrierService(zipcode, carrierServiceId, request);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder().postalCodeCarrierServices(List.of(dto)).build();

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{zipcode}/{carrierServiceId}")
  @Operation(
      summary = "Delete Postal Code Carrier Service",
      description = PostalCodeCarrierServiceConstants.DELETE_POSTAL_CODE_CARRIER_SERVICE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.DELETE_POSTAL_CODE_CARRIER_SERVICE_SUCCESS)
  public ResponseEntity<PostalCodeCarrierServiceResponse> deletePostalCodeCarrierService(
      @Parameter(
              description = PostalCodeCarrierServiceConstants.ZIP_CODE,
              example = PostalCodeCarrierServiceConstants.ZIP_CODE_EXAMPLE)
          @PathVariable
          String zipcode,
      @Parameter(
              description = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID,
              example = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID_EXAMPLE)
          @PathVariable
          String carrierServiceId) {

    log.info(
        "Deleting postal code carrier service for zipcode: {} and carrier service: {}",
        zipcode,
        carrierServiceId);

    postalCodeCarrierService.deletePostalCodeCarrierService(zipcode, carrierServiceId);

    PostalCodeCarrierServiceResponse response = PostalCodeCarrierServiceResponse.builder().build();

    return ResponseEntity.ok(response);
  }

  @GetMapping
  @Operation(
      summary = "Get Postal Code Carrier Services",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_PAGE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_PAGE_SUCCESS)
  public ResponseEntity<PostalCodeCarrierServiceResponse> getPostalCodeCarrierServices(
      Pageable pageable) {

    log.info("Getting postal code carrier services with pageable: {}", pageable);

    Page<PostalCodeCarrierServiceDto> dtoPage =
        postalCodeCarrierService.getPostalCodeCarrierServices(pageable);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder()
            .postalCodeCarrierServices(dtoPage.getContent())
            .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/by-zipcode")
  @Operation(
      summary = "Get Postal Code Carrier Services by Zipcode",
      description = "Retrieves all postal code carrier service mappings for a specific zipcode")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved postal code carrier services by zipcode")
  public ResponseEntity<PostalCodeCarrierServiceResponse> getPostalCodeCarrierServicesByZipcode(
      @Parameter(
              description = PostalCodeCarrierServiceConstants.ZIP_CODE,
              example = PostalCodeCarrierServiceConstants.ZIP_CODE_EXAMPLE)
          @RequestParam
          String zipcode) {

    log.info("Getting postal code carrier services by zipcode: {}", zipcode);

    List<PostalCodeCarrierServiceDto> dtos =
        postalCodeCarrierService.getPostalCodeCarrierServicesByZipcode(zipcode);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder().postalCodeCarrierServices(dtos).build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/by-carrier-service")
  @Operation(
      summary = "Get Postal Code Carrier Services by Carrier Service ID",
      description =
          "Retrieves all postal code carrier service mappings for a specific carrier service")
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved postal code carrier services by carrier service ID")
  public ResponseEntity<PostalCodeCarrierServiceResponse>
      getPostalCodeCarrierServicesByCarrierServiceId(
          @Parameter(
                  description = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID,
                  example = PostalCodeCarrierServiceConstants.CARRIER_SERVICE_ID_EXAMPLE)
              @RequestParam
              String carrierServiceId) {

    log.info("Getting postal code carrier services by carrier service id: {}", carrierServiceId);

    List<PostalCodeCarrierServiceDto> dtos =
        postalCodeCarrierService.getPostalCodeCarrierServicesByCarrierServiceId(carrierServiceId);

    PostalCodeCarrierServiceResponse response =
        PostalCodeCarrierServiceResponse.builder().postalCodeCarrierServices(dtos).build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/cache-keys")
  @Operation(
      summary = "Get Cache Keys",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_CACHE_DESC)
  @ApiResponse(
      responseCode = "200",
      description = PostalCodeCarrierServiceConstants.GET_POSTAL_CODE_CARRIER_SERVICE_CACHE_SUCCESS)
  public ResponseEntity<List<PostalCodeCarrierServiceCacheKeyDto>> getCacheKeys() {

    log.info("Getting all cache keys for postal code carrier services");

    List<PostalCodeCarrierServiceCacheKeyDto> cacheKeys =
        postalCodeCarrierService.getAllCacheKeys();

    return ResponseEntity.ok(cacheKeys);
  }
}
