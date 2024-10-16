/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.controller;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import com.nextuple.promise.sourcing.rule.controller.docs.AddSourcingConstraintDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.DeleteSourcingConstraintDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetSourcingConstraintDetailsDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.GetSourcingConstraintListDoc;
import com.nextuple.promise.sourcing.rule.controller.docs.UpdateSourcingConstraintDoc;
import com.nextuple.promise.sourcing.rule.service.SourcingConstraintService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@RequestMapping("/sourcing-constraint")
@RequiredArgsConstructor
@Tag(name = "Sourcing Constraint APIs")
public class SourcingConstraintController {

  private static final Logger logger = LoggerFactory.getLogger(SourcingConstraintController.class);

  private final SourcingConstraintService sourcingConstraintService;

  @AddSourcingConstraintDoc
  @PostMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> addSourcingConstraint(
      @Valid @RequestBody SourcingConstraintRequest sourcingConstraintRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("Processing add sourcing constraint request");
    try {
      var sourcingConstraintDetailsResponse =
          sourcingConstraintService.processAddSourcingConstraint(sourcingConstraintRequest);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing constraint successfully added")
              .payload(sourcingConstraintDetailsResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process add sourcing constraint request", e);
      throw e;
    }
  }

  @GetSourcingConstraintListDoc
  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingConstraintsResponse>> fetchSourcingConstraintsList(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "groupId can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Unique identifier for the group for which constraints has to be applied.")
          String groupId)
      throws PromiseEngineException {
    logger.debug("-- Processing fetch sourcing constraints list request");
    try {
      var sourcingConstraintsResponse =
          sourcingConstraintService.processFetchSourcingConstraintsList(orgId, groupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("List of sourcing constraints fetched successfully")
              .payload(sourcingConstraintsResponse)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process fetch sourcing constraints list request", e);
      throw e;
    }
  }

  @GetSourcingConstraintDetailsDoc
  @GetMapping(value = "/orgId/{orgId}/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>>
      fetchSourcingConstraintByOrgIdAndId(
          @NotBlank(message = "orgId can't be empty")
              @PathVariable
              @Parameter(
                  description = "Unique identifier of the organization.",
                  example = "NEXTUPLE")
              String orgId,
          @NotNull(message = "id can't be empty")
              @Min(value = 0)
              @PathVariable
              @Parameter(description = "Unique identifier for the sourcing constraint.")
              Long id)
          throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing get sourcing rule by orgId and id request");
    try {
      var sourcingConstraintDetails =
          sourcingConstraintService.processFetchSourcingConstraintsByIdAndOrgId(id, orgId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing constraint details fetched successfully")
              .payload(sourcingConstraintDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process get sourcing constraint details by id request", e);
      throw e;
    }
  }

  @UpdateSourcingConstraintDoc
  @PutMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> updateSourcingConstraint(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "groupId can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Unique identifier for the group for which constraints has to be applied.")
          String groupId,
      @NotNull(message = "sourcingConstraint can't be empty")
          @RequestParam
          @Parameter(description = "Defines the constraint to be added.")
          SourcingConstraintEnum sourcingConstraint,
      @Valid @RequestBody SourcingConstraintUpdationRequest updationRequest)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing update sourcing constraint request");
    try {
      var updateSourcingConstraintDetails =
          sourcingConstraintService.processUpdateSourcingConstraint(
              orgId, sourcingConstraint, updationRequest, groupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing Constraint updated successfully")
              .payload(updateSourcingConstraintDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to process update sourcing constraint request", e);
      throw e;
    }
  }

  @DeleteSourcingConstraintDoc
  @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse<SourcingConstraintDetailsResponse>> deleteSourcingConstraint(
      @NotBlank(message = "orgId can't be empty")
          @RequestParam
          @Parameter(description = "Unique identifier for organization.", example = "NEXTUPLE")
          String orgId,
      @NotBlank(message = "groupId can't be empty")
          @RequestParam
          @Parameter(
              description =
                  "Unique identifier for the group for which constraints has to be applied.")
          String groupId,
      @NotNull(message = "sourcingConstraint can't be empty")
          @RequestParam
          @Parameter(description = "Defines the constraint to be added.")
          SourcingConstraintEnum sourcingConstraint)
      throws PromiseEngineException, CommonServiceException {
    logger.debug("-- Processing delete sourcing constraint request");
    try {
      var deleteSourcingConstraintDetails =
          sourcingConstraintService.processDeleteSourcingConstraintDetails(
              orgId, sourcingConstraint, groupId);
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Sourcing Constraint successfully deleted")
              .payload(deleteSourcingConstraintDetails)
              .build());
    } catch (Exception e) {
      logger.error("Failed to delete sourcing constraint details", e);
      throw e;
    }
  }
}
