package com.nextuple.node.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.node.controller.docs.CreateNodeDoc;
import com.nextuple.node.controller.docs.DeleteNodeDoc;
import com.nextuple.node.controller.docs.GetNodeDetailsDoc;
import com.nextuple.node.controller.docs.UpdateNodeDetailsDoc;
import com.nextuple.node.domain.NodeConstants;
import com.nextuple.node.domain.inbound.VendorRequest;
import com.nextuple.node.domain.inbound.VendorUpdationRequest;
import com.nextuple.node.domain.outbound.VendorResponse;
import com.nextuple.node.persistence.exception.VendorDomainException;
import com.nextuple.node.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(name = "Vendor APIs")
@RequestMapping("/vendor")
@RequiredArgsConstructor
public class VendorController {

  private static final Logger logger = LoggerFactory.getLogger(VendorController.class);
  private final VendorService vendorService;

  @CreateNodeDoc
  @PostMapping
  public ResponseEntity<BaseResponse<VendorResponse>> createVendor(
      @Valid @RequestBody VendorRequest vendorRequest)
      throws CommonServiceException, VendorDomainException {
    logger.debug("Processing vendor creation request");
    try {
      var vendorResponse = vendorService.createVendor(vendorRequest);
      logger.info("Response after creation of vendor :{}", vendorResponse);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor successfully created")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create vendor");
      throw e;
    }
  }

  @UpdateNodeDetailsDoc
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
    logger.debug("Processing update vendor details");
    try {
      var vendorResponse =
          vendorService.updateVendorDetails(vendorId, orgId, vendorUpdationRequest);
      logger.info("Response after updation of vendor :{}", vendorResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor details updated successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to update vendor details");
      throw e;
    }
  }

  @GetMapping("/{vendorId}/{orgId}")
  @GetNodeDetailsDoc
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
    logger.debug("Processing get vendor details");
    try {
      var vendorResponse = vendorService.getVendorDetails(vendorId, orgId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor details fetched successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to fetch vendor details");
      throw e;
    }
  }

  @Operation(
      summary = "Delete Vendor",
      description = "Deletes an existing vendor when it is no longer required.")
  @ApiResponse(responseCode = "200", description = NodeConstants.DELETE_NODE_SUCCESS)
  @DeleteNodeDoc
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
    logger.debug("Processing delete vendor");
    try {

      var vendorResponse = vendorService.deleteVendor(vendorId, orgId);
      logger.info("Response after deletion of vendor :{}", vendorResponse);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Vendor deleted successfully")
              .payload(vendorResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete vendor");
      throw e;
    }
  }
}
