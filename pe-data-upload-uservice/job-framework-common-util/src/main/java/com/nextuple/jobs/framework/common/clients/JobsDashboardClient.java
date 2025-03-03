/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.clients;

import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.jobs.framework.common.inbound.FileMetaDataCreationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-jobs-dashboard",
    url = "${spring.application.dependencies.data-upload:http://pe-data-upload-uservice:8080/}")
public interface JobsDashboardClient {

  @GetMapping("/org/{orgId}/jobs/{jobId}")
  BaseResponse<JobDto> getJob(
      @NotEmpty @NotNull @PathVariable String orgId, @NotEmpty @NotNull @PathVariable String jobId);

  @PostMapping("/org/{orgId}/jobs")
  BaseResponse<JobResponse> processJobJsonOffline(
      @NotNull @Valid @RequestParam JobTypeEnum jobType,
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestBody String request);

  @PutMapping(
      value = "/org/{orgId}/jobs/{jobId}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  BaseResponse<JobResponse> processJobJsonByScheduler(
      @NotEmpty @NotNull @PathVariable String orgId,
      @NotNull @Valid @RequestParam JobTypeEnum jobType,
      @PathVariable String jobId);

  @GetMapping("/org/{orgId}/jobs/filters")
  BaseResponse<PagePayload<JobResponse>> getJobsByFilter(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam(required = false) String jobType,
      @RequestParam(required = false) Integer days,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false) String sortOrder,
      @RequestParam(required = false) int pageNo,
      @RequestParam(required = false) int pageSize);

  @GetMapping("/org/{orgId}/jobs-dashboard/{jobId}/results")
  BaseResponse<List<RecordStatusDto>> getJobRecords(
      @NotEmpty @NotNull @PathVariable String orgId,
      @NotEmpty @NotNull @PathVariable String jobId,
      @RequestParam(required = false) String status);

  @PostMapping("/org/{orgId}/jobs")
  BaseResponse<JobResponse> processJobOffline(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam @NotNull @Valid JobTypeEnum jobType,
      @RequestBody byte[] csvFile,
      @RequestParam String fileName);

  @PostMapping("v1/org/{orgId}/jobs")
  BaseResponse<JobResponse> processJobOfflineWithFileMetaDataId(
      @NotEmpty @NotNull @PathVariable String orgId,
      @RequestParam @NotNull JobTypeEnum jobType,
      @RequestParam @NotNull long fileMetadataId);

  @PostMapping("/file-metadata")
  BaseResponse<FileMetaDataResponse> createFileMetadata(
      @Valid @RequestBody FileMetaDataCreationRequest fileMetadataCreationRequest);

  @GetMapping("/file-metadata/{id}")
  BaseResponse<FileMetaDataResponse> findFileMetadataById(@NotNull @PathVariable Long id);

  @GetMapping("/v1/org/{orgId}/jobs-dashboard/{jobId}/results")
  BaseResponse<PagePayload<RecordStatusDto>> getJobRecordsByFilters(
      @NotEmpty @NotNull @PathVariable String orgId,
      @NotEmpty @NotNull @PathVariable String jobId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Integer pageNo,
      @RequestParam(required = false) Integer pageSize);
}
