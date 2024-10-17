/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.dataupload.common.feign;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.NodeAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-data-upload-api",
    url = "${spring.application.dependencies.data-upload:http://pe-data-upload-uservice:8080/}")
public interface DataUploadFeign {

  @GetMapping("/ui/node-carrier-service-option/{orgId}")
  BaseResponse<PagePayload<NodeCarrierServiceAndServiceOptionResponse>>
      getListOfNodeCarrierServiceAndServiceOptionDetails(
          @NotEmpty @NotNull @PathVariable String orgId,
          @RequestParam(required = false) Integer pageNo,
          @RequestParam(required = false) Integer pageSize,
          @RequestParam(required = false) String sortBy,
          @RequestParam(required = false) String sortOrder);

  @GetMapping("/ui/node-service-option/orgId/{orgId}")
  BaseResponse<PagePayload<NodeAndServiceOptionResponse>> getNodeServiceOption(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/ui/processing-time-buffer/orgId/{orgId}")
  BaseResponse<PagePayload<ProcessingTimeBufferResponse>> getProcessingTimeBufferDetails(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);

  @GetMapping("/ui/processing-time-buffer/v1/orgId/{orgId}")
  BaseResponse<PagePayload<ProcessingTimeBufferResponse>> getProcessingTimeBufferDetailsV1(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam(required = false) String nodeIds,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) String sortBy,
      @RequestParam(required = false) String sortOrder);
}
