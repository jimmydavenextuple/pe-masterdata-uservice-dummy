/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.domain.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NodeCalendarRequest extends AdditionalAttributes implements Serializable {

  @NotBlank(message = "calendarId can't be blank")
  @Length(max = 40)
  @Schema(
      description = "Unique identifier of the calendar.",
      maxLength = 40,
      example = "CALENDAR2023")
  private String calendarId;

  @NotBlank(message = "nodeId can't be blank")
  @Length(max = 40)
  @Schema(description = "Unique identifier of the node.", maxLength = 40, example = "NODE01")
  private String nodeId;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 40)
  @Schema(
      description = "Unique identifier of the organization.",
      maxLength = 40,
      example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "effectiveDate can't be blank")
  @Schema(description = "Effective date of the calendar.", example = "2023-01-01")
  private String effectiveDate;

  @Schema(description = "Description of the node calendar.", example = "Calendar for NODE01")
  private String description;
}
