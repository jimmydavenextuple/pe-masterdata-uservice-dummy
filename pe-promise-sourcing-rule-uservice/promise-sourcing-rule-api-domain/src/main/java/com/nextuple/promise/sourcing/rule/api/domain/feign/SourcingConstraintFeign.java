/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.enums.SourcingConstraintEnum;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintRequest;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.SourcingConstraintUpdationRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.SourcingConstraintsResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-promise-sourcing-rule-uservice",
    url =
        "${spring.application.dependencies.promise-sourcing-rule:http://pe-promise-sourcing-rule-uservice:8080/}")
public interface SourcingConstraintFeign {

  @PostMapping("/sourcing-constraint")
  BaseResponse<SourcingConstraintDetailsResponse> addSourcingConstraint(
      @Valid @RequestBody SourcingConstraintRequest sourcingConstraintRequest);

  @GetMapping("/sourcing-constraint/list")
  BaseResponse<SourcingConstraintsResponse> fetchSourcingConstraintsList(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String groupId);

  @GetMapping("/sourcing-constraint/orgId/{orgId}/id/{id}")
  BaseResponse<SourcingConstraintDetailsResponse> fetchSourcingConstraintByOrgIdAndId(
      @NotBlank @PathVariable String orgId, @NotNull @PathVariable Long id);

  @PutMapping("/sourcing-constraint")
  BaseResponse<SourcingConstraintDetailsResponse> updateSourcingConstraint(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String groupId,
      @NotNull @RequestParam SourcingConstraintEnum sourcingConstraint,
      @Valid @RequestBody SourcingConstraintUpdationRequest updationRequest);

  @DeleteMapping("/sourcing-constraint")
  BaseResponse<SourcingConstraintDetailsResponse> deleteSourcingConstraint(
      @NotBlank @RequestParam String orgId,
      @NotBlank @RequestParam String groupId,
      @NotNull @RequestParam SourcingConstraintEnum sourcingConstraint);
}
