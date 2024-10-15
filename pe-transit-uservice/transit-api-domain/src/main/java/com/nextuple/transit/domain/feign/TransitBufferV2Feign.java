/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.feign;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.transit.domain.inbound.TransitBufferDeletionRequest;
import com.nextuple.transit.domain.inbound.TransitBufferRequest;
import com.nextuple.transit.domain.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.outbound.TransitBufferV2Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
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
@ConditionalOnProperty(
    prefix = "transit.buffer",
    name = "version",
    havingValue = "v2",
    matchIfMissing = true)
public interface TransitBufferV2Feign
    extends ITransitBufferFeign<TransitBufferV2Response, TransitBufferRequest> {

  @PostMapping("/v2/transit/buffer")
  BaseResponse<TransitBufferV2Response> createTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);

  @GetMapping("/v2/transit/buffer/{orgId}/{destinationGeozone}")
  BaseResponse<List<TransitBufferDetailsResponse>>
      getTransitBuffersByOrgIdAndDestinationGeozoneAndRequestDateAndHorizonDays(
          @PathVariable String orgId,
          @PathVariable String destinationGeozone,
          @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate requestDate,
          @Min(value = 0, message = "horizonDays can't be negative") @RequestParam
              Integer horizonDays);

  @PutMapping("/v2/transit/buffer")
  BaseResponse<TransitBufferV2Response> updateTransitBuffer(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);

  @DeleteMapping("/v2/transit/buffer")
  BaseResponse<TransitBufferV2Response> deleteTransitBufferDetails(
      @Valid @RequestBody TransitBufferRequest transitBufferRequest);

  @GetMapping("/transit/v1/buffer/{transitBufferConfigRequestId}")
  BaseResponse<PreSignedUrlResponse> getTransitBufferDetails(
      @PathVariable Long transitBufferConfigRequestId, @RequestParam String createdBy);

  @DeleteMapping("/v2/transit/buffer/one")
  BaseResponse<TransitBufferV2Response> deleteTransitBufferRecord(
      @Valid @RequestBody TransitBufferDeletionRequest transitBufferDeletionRequest);
}
