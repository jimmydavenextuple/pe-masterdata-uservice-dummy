/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.inbound.DistinctGeozonesResponse;
import com.nextuple.transit.domain.inbound.TransitBufferCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataCreationRequest;
import com.nextuple.transit.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.transit.domain.inbound.TransitDetailsRequest;
import com.nextuple.transit.domain.outbound.TransitResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface TransitFeign {

  @PostMapping("/transit")
  BaseResponse<TransitResponse> addTransitData(
      @RequestBody TransitDataCreationRequest transitDataCreationRequest);

  @PutMapping("/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<TransitResponse> updateTransitData(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId,
      @RequestBody TransitDataUpdationRequest transitDataUpdationRequest);

  @GetMapping(
      "/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}/{serviceOption}")
  BaseResponse<TransitResponse> getTransitDetails(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId,
      @PathVariable String serviceOption);

  @DeleteMapping("/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<TransitResponse> deleteTransitDetails(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId);

  @GetMapping("/transit/{orgId}/{destinationGeozone}/")
  BaseResponse<List<TransitResponse>> getTransitDetailsList(
      @PathVariable String orgId,
      @PathVariable String destinationGeozone,
      @RequestParam List<String> sourceGeozones);

  @PostMapping("/transit/distinct/dFSA/{orgId}/{sourceGeozone}")
  BaseResponse<List<String>> getDistinctDestinationGeoZones(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @RequestBody List<String> carrierServiceId);

  @GetMapping("/transit/transit-entries/{orgId}/{carrierServiceId}")
  BaseResponse<TransitTimeEntriesDto> getTransitTimeEntries(
      @PathVariable String orgId, @PathVariable String carrierServiceId);

  @GetMapping("/transit/{orgId}/{destinationGeozone}/")
  BaseResponse<List<TransitResponse>> getTransitDetailsListForDestinationGeoZone(
      @PathVariable String orgId, @PathVariable String destinationGeozone);

  @PutMapping("/transit/buffer")
  BaseResponse<TransitResponse> updateTransitBufferDetails(
      @Valid @RequestBody TransitBufferCreationRequest transitBufferCreationRequest);

  @PostMapping("/transit/transit-entries/{orgId}/{carrierServiceId}/geozones")
  BaseResponse<List<TransitResponse>> getTransitTimeDetailsForDestinationGeoZonesList(
      @PathVariable String orgId,
      @PathVariable String carrierServiceId,
      @RequestBody TransitDetailsRequest transitDetailsRequest);

  @PutMapping("/transit/{orgId}/{carrierServiceId}/buffer-days")
  BaseResponse<TransitResponse> updateTransitBufferDays(
      @PathVariable String orgId,
      @PathVariable String carrierServiceId,
      @RequestParam String sourceGeoZone,
      @RequestParam String destinationGeoZone);

  @GetMapping("/transit")
  BaseResponse<DistinctGeozonesResponse> getDistinctSourceAndDestinationGeozones(
      @RequestParam String orgId, @RequestParam String carrierServiceId);

  @GetMapping("/transit/v2/{orgId}/{destinationGeozone}")
  BaseResponse<List<TransitResponse>> getListOfTransitDetailsForDestinationGeoZone(
      @PathVariable String orgId, @PathVariable String destinationGeozone);
}
