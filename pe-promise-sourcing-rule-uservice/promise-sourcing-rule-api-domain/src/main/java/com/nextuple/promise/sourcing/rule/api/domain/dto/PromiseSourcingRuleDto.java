/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PromiseSourcingRuleDto {

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "List of service options.", example = "SDND , NEXTDAY , EXPRESS")
  private String serviceOption;

  @Schema(description = "Destination geozone for a given region.", example = "VOA")
  private String destinationGeoZone;

  @Schema(description = "Set of source nodes.", example = "[1001,4567]")
  private Set<String> sourceNodes;

  @Schema(description = "Priority of the given source nodes.", example = "1,2,3")
  private int priority;

  @Schema(description = "AllocationRuleId of a given sourcing rule.", example = "DEFAULT")
  private String allocationRuleId;
}
