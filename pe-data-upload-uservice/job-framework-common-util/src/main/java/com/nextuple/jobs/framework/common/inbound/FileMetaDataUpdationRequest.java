/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.framework.common.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FileMetaDataUpdationRequest implements Serializable {

  private static final long serialVersionUID = -2149282044801518838L;

  @Schema(description = "Name of the API.", example = "ui/transit/2022-12-22/transit.csv")
  private String name;

  @Schema(
      description = "Total path of the API.",
      example = "dataupload/ui/transit/2022-12-22/transit.csv")
  private String path;

  @Schema(description = "Size of the file.", example = "435")
  private String size;

  @Schema(description = "Type of the file.", example = "text/csv")
  private String type;

  @Schema(description = "Type of storage.", example = "BLOB")
  private String storageType;

  @Schema(description = "Details of the file uploaded by UI.")
  private String description;

  @Schema(description = "Unique ID of the file.")
  private String extReferenceId;

  @Schema(description = "Unique ID of the parent file.")
  private Long parentFileId;

  @Schema(description = "Name of the user.")
  private String updatedBy;
}
