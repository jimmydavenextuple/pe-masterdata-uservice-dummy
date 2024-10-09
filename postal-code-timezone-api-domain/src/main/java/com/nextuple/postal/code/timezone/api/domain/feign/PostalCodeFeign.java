/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.inbound.CustomRegionRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.PostalCodeRequest;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    name = "pe-postal-code-timezone-uservice",
    url =
        "${spring.application.dependencies.postal-code-timezone:http://pe-postal-code-timezone-uservice:8080/}")
public interface PostalCodeFeign {
  @PostMapping("/postal-code")
  BaseResponse<PostalCodeResponse> createPostalCode(
      @Valid @RequestBody PostalCodeRequest postalCodeRequest);

  @PutMapping("/postal-code/update")
  BaseResponse<PostalCodeResponse> updatePostalCode(
      @Valid @RequestBody PostalCodeRequest postalCodeRequest);

  @GetMapping("/postal-code/{orgId}/{postalCode}")
  BaseResponse<PostalCodeResponse> getPostalCode(
      @NotNull @PathVariable String orgId, @NotNull @PathVariable String postalCode);

  @DeleteMapping("/postal-code/{orgId}/{postalCode}")
  BaseResponse<PostalCodeResponse> deletePostalCode(
      @NotNull @PathVariable String orgId, @NotNull @PathVariable String postalCode);

  @GetMapping("/postal-code/orgId/{orgId}/postal-code/{postalCode}")
  BaseResponse<CustomRegionResponse> fetchCustomRegionIdByPostalCode(
      @PathVariable String orgId, @PathVariable String postalCode);

  @GetMapping("/custom-region/orgId/{orgId}/custom-region/{id}")
  BaseResponse<CustomRegionResponse> fetchCustomRegionDetailsByOrgIdAndId(
      @PathVariable String orgId, @PathVariable String id);

  @GetMapping("/postal-code/{orgId}")
  BaseResponse<List<PostalCodeResponse>> getByPostalCodePrefix(
      @PathVariable String orgId, @RequestParam String zipCodePrefix);

  @GetMapping("/postal-code/list/org/{orgId}")
  BaseResponse<List<String>> getPostalCodePrefixForOrgIdAndState(
      @PathVariable String orgId, @RequestParam String state);

  @GetMapping("/postal-code/market-regions/org/{orgId}")
  BaseResponse<List<MarketRegionInfo>> getMarketRegionsForOrgId(@PathVariable String orgId);

  @GetMapping("/postal-code/market-region/org/{orgId}")
  BaseResponse<List<PostalCodeResponse>> getPostalCodeTimeZoneForOrgIdAndCountry(
      @NotBlank @RequestParam String orgId, @NotBlank @RequestParam String country);

  @PostMapping("/custom-region")
  BaseResponse<CustomRegionResponse> createCustomRegion(
      @Valid @RequestBody CustomRegionRequest customRegionRequest);

  @PutMapping("/custom-region")
  BaseResponse<CustomRegionResponse> updateCustomRegion(
      @Valid @RequestBody CustomRegionRequest customRegionRequest);

  @DeleteMapping("/custom-region/orgId/{orgId}/custom-region/{id}")
  BaseResponse<CustomRegionResponse> deleteCustomRegion(
      @NotBlank @PathVariable String orgId, @NotBlank @PathVariable String id);

  @GetMapping("/custom-region/list/orgId/{orgId}")
  BaseResponse<PagePayload<CustomRegionDto>> getCustomRegionList(
      @NotBlank @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);
}
