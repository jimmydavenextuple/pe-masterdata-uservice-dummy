/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.inbound;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TransitBufferConfigRequest extends AdditionalAttributes implements Serializable {

  private static final long serialVersionUID = -1514262691280383765L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  @Length(max = 50)
  @NotBlank(message = "orgId can't be empty")
  private String orgId;

  @Schema(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
  @Length(max = 50)
  @NotBlank(message = "carrierServiceId can't be empty")
  private String carrierServiceId;

  @Schema(description = "Buffer days for the transit.", example = "2.1")
  @NotNull
  private Double bufferDays;

  @Schema(
      description = "The start date of the transit buffer in UTC format.",
      example = "2023-12-24T11:58:22Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  @NotNull
  private Date startDate;

  @Schema(
      description = "The end date of the transit buffer in UTC format.",
      example = "2023-12-26T11:58:22Z")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", lenient = OptBoolean.FALSE)
  @NotNull
  private Date endDate;

  @Schema(description = "Unique identifier of the transit buffer request.", example = "1231231231")
  private Long transitBufferRequestId;

  @Schema(description = "File path for the uploaded file.", example = "transit/buffer.csv")
  private String filePath;

  @Schema(description = "Storage type of the uploaded file.", example = "Azure")
  private String storageType;

  @Schema(description = "User who created the file.", example = "user@email.com")
  @NotBlank(message = "createdBy can't be blank")
  private String createdBy;

  @Schema(description = "Action to be performed on the uploaded file.", example = "CREATE")
  private String action;
}
