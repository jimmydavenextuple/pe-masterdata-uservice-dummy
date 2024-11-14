/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePromiseSourcingRuleRequest implements Serializable {
  private static final long serialVersionUID = 8787181446707914255L;

  @NotBlank(message = "serviceOptionEnum can't be empty.")
  @Schema(description = "List of service options.", example = "SDND , NEXTDAY , EXPRESS")
  private String serviceOption;

  @NotBlank(message = "orgId can't be empty.")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "destinationGeoZone can't be empty.")
  @Schema(description = "Destination geozone for a given region.", example = "VOA")
  private String destinationGeoZone;

  @NotEmpty(message = "sourceNodes can't be empty.")
  @Schema(description = "Set of source nodes.", example = "[1001,4567]")
  @Size(max = 1000, message = "sourceNodes' size should not be more than 1000.")
  private Set<String> sourceNodes;

  @NotNull(message = "priority can't be null.")
  @Min(value = 0, message = "priority should be more than 0.")
  @Schema(description = "Priority of the given source nodes.", example = "1,2,3")
  private int priority;

  @Schema(description = "AllocationRuleId of a given sourcing rule.", example = "DEFAULT")
  private String allocationRuleId;
}
