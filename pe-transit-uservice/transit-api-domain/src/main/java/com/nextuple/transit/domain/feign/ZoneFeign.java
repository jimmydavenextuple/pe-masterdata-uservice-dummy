/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.ZoneRequest;
import com.nextuple.transit.domain.inbound.ZoneUpdateRequest;
import com.nextuple.transit.domain.outbound.ZoneResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pe-transit-uservice",
    url = "${spring.application.dependencies.transit:http://pe-transit-uservice:8080/}")
public interface ZoneFeign {

  @PostMapping("/zone")
  BaseResponse<ZoneResponse> addZoneData(@RequestBody ZoneRequest zoneRequest);

  @PutMapping("/zone/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<ZoneResponse> updateZone(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId,
      @RequestBody ZoneUpdateRequest zoneUpdateRequest);

  @GetMapping("/zone/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<ZoneResponse> getZoneDetails(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId);

  @DeleteMapping("/zone/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<ZoneResponse> deleteZoneDetails(
      @PathVariable String orgId,
      @PathVariable String sourceGeozone,
      @PathVariable String destinationGeozone,
      @PathVariable String carrierServiceId);

  @GetMapping("/zone/{orgId}/{destinationGeozone}")
  BaseResponse<List<ZoneResponse>> getZoneDetailsList(
      @PathVariable String orgId, @PathVariable String destinationGeozone);
}
