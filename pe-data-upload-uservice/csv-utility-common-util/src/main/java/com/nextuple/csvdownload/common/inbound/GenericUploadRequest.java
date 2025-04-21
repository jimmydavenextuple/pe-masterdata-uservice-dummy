/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.common.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericUploadRequest {

  @Schema(description = "Unique identifier for organisation", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Path of the file", example = "/ui/dataupload/postal-code")
  private String filePath;

  @Schema(description = "Type of the storage", example = "Blob")
  private String storageType;

  @Schema(description = "Name of the user", example = "ABC")
  private String createdBy;

  @Schema(description = "Additional data of the file")
  private Map<String, String> additionalReference;

  public String getBucketName() {
    return splitFilePath()[0];
  }

  public String getFilePathUrl() {
    return splitFilePath()[1];
  }

  private String[] splitFilePath() {
    if (this.filePath == null || !this.filePath.contains("/")) {
      throw new IllegalArgumentException("Invalid file path: " + this.filePath);
    }
    return this.filePath.split("/", 2);
  }
}
