/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.mapper;

import com.amazonaws.services.s3.model.S3Object;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse.FileResponseBuilder;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMapper {

  @Mapping(target = "filePath", source = "key")
  @Mapping(target = "contentType", source = "objectMetadata.contentType")
  @Mapping(target = "contentLength", source = "objectMetadata.contentLength")
  @Mapping(target = "lastModifiedDate", source = "objectMetadata.lastModified")
  @Mapping(target = "inputStream", ignore = true)
  FileResponse mapToFileDownloadResponse(S3Object s3Object);

  @AfterMapping
  default void afterMappingToFileDownloadResponse(
      @MappingTarget FileResponseBuilder fileDownloadResponse, S3Object s3Object) {
    fileDownloadResponse.inputStream(s3Object.getObjectContent());
    String[] fileArray = s3Object.getKey().split("/", 0);
    fileDownloadResponse.fileName(fileArray[fileArray.length - 1]);
  }
}
