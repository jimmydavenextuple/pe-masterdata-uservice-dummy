/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.clients;

import com.nextuple.common.constants.CommonConstants;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "pe-config-jobs-dashboard",
    url = "${spring.application.dependencies.data-upload:http://pe-data-upload-uservice:8080/}")
public interface FileMetaDataClient {

  @PostMapping("/file-metadata")
  BaseResponse<FileMetaDataResponse> createFileMetadata(
      @Valid @RequestBody FileMetaDataCreationRequest fileMetadataCreationRequest);

  @GetMapping("/file-metadata/{id}")
  BaseResponse<FileMetaDataResponse> findFileMetadataById(@NotBlank @PathVariable Long id);

  @GetMapping("/file-metadata/{id}")
  BaseResponse<FileMetaDataResponse> findFileMetadataById(
      @NotBlank @PathVariable Long id,
      @RequestHeader(CommonConstants.HEADER_TENANT_ID) String tenantId);
}
