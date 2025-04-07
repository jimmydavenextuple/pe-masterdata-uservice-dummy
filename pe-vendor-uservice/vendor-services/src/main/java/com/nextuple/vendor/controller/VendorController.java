/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.vendor.controller.docs.CreateVendorDoc;
import com.nextuple.vendor.controller.docs.DeleteVendorDoc;
import com.nextuple.vendor.controller.docs.GetVendorDetailsDoc;
import com.nextuple.vendor.controller.docs.UpdateVendorDetailsDoc;
import com.nextuple.vendor.domain.VendorConstants;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Vendor APIs")
@RequestMapping("/vendor")
@RequiredArgsConstructor
@Slf4j
public class VendorController {
  private final VendorService vendorService;

  @CreateVendorDoc
  @PostMapping
  public ResponseEntity<BaseResponse<VendorResponse>> createVendor(
      @Valid @RequestBody VendorRequest vendorRequest) throws VendorDomainException {
    log.debug("Processing vendor creation request");
    try {
      var vendorResponse = vendorService.createVendor(vendorRequest);
      log.debug("Response after creation of vendor :{}", vendorResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor successfully created")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      log.error("Failed to create vendor");
      throw e;
    }
  }

  @UpdateVendorDetailsDoc
  @PutMapping("/{vendorId}/{orgId}")
  public ResponseEntity<BaseResponse<VendorResponse>> updateVendorDetails(
      @NotBlank(message = "vendorId can't be empty")
          @Parameter(description = "Unique identifier of the vendor.", example = "Vendor-01")
          @PathVariable
          String vendorId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId,
      @Valid @RequestBody VendorUpdationRequest vendorUpdationRequest)
      throws VendorDomainException, CommonServiceException {
    log.debug("Processing update vendor details");
    try {
      var vendorResponse =
          vendorService.updateVendorDetails(vendorId, orgId, vendorUpdationRequest);
      log.debug("Response after updation of vendor :{}", vendorResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor details updated successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      log.error("Failed to update vendor details");
      throw e;
    }
  }

  @GetVendorDetailsDoc
  @GetMapping("/{vendorId}/{orgId}")
  public ResponseEntity<BaseResponse<VendorResponse>> getVendorDetails(
      @NotBlank(message = "vendorId can't be empty")
          @Parameter(description = "Unique identifier of the vendor.", example = "Vendor-01")
          @PathVariable
          String vendorId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId)
      throws VendorDomainException, CommonServiceException {
    log.debug("Processing get vendor details");
    try {
      var vendorResponse = vendorService.getVendorDetails(vendorId, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor details fetched successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      log.error("Failed to fetch vendor details");
      throw e;
    }
  }

  @Operation(
      summary = "Delete Vendor",
      description = "Deletes an existing vendor when it is no longer required.")
  @ApiResponse(responseCode = "200", description = VendorConstants.DELETE_VENDOR_SUCCESS)
  @DeleteVendorDoc
  @DeleteMapping("/{vendorId}/{orgId}")
  public ResponseEntity<BaseResponse<VendorResponse>> deleteVendor(
      @NotBlank(message = "vendorId can't be empty")
          @Parameter(description = "Unique identifier of the vendor.", example = "Vendor-01")
          @PathVariable
          String vendorId,
      @NotBlank(message = "orgId can't be empty")
          @Parameter(description = "Unique identifier of the organization.", example = "NEXTUPLE")
          @PathVariable
          String orgId)
      throws VendorDomainException, CommonServiceException {
    log.debug("Processing delete vendor");
    try {
      var vendorResponse = vendorService.deleteVendor(vendorId, orgId);
      log.debug("Response after deletion of vendor :{}", vendorResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor deleted successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      log.error("Failed to delete vendor");
      throw e;
    }
  }
}
