/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.outbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferConfigResponse extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = 1291152938195817113L;

  @Schema(description = "Unique identifier of the transit buffer.", example = "2")
  private Long id;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
  private String carrierServiceId;

  @Schema(description = "Buffer days for the transit.", example = "2.1")
  private Double bufferDays;

  @Schema(
      description = "The start date of the transit buffer in UTC format.",
      example = "2023-12-24T11:58:22Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date startDate;

  @Schema(
      description = "The end date of the transit buffer in UTC format.",
      example = "2023-12-26T11:58:22Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  private Date endDate;

  @Schema(
      description = "Status of the transit buffer configuration request.",
      allowableValues = {"CREATED", "INPROGRESS", "COMPLETED", "DELETED", "INACTIVE", "ERROR"})
  private TransitBufferConfigRequestStatusEnum status;

  @Schema(description = "Unique identifier of the parent request", example = "211212")
  @JsonInclude(Include.NON_NULL)
  private Long parentRequestId;

  @Schema(description = "Unique identifier of the file metadata.", example = "211213")
  private Long fileMetaDataId;

  @Schema(description = "Name of the uploaded file.", example = "file.csv")
  private String fileName;
}
