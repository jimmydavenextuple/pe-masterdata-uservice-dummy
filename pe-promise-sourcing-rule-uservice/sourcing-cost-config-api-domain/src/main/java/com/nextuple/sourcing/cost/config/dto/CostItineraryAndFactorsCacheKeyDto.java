/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.dto;

import com.nextuple.sourcing.cost.config.enums.ItineraryStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostItineraryAndFactorsCacheKeyDto implements Serializable {
  private static final long serialVersionUID = 726315503643242061L;

  @Schema(description = "Unique identifier of the organization", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Cost itinerary to determine the cost", example = "SHIPPING_COST_UPSLIKE")
  private String costItinerary;

  @Schema(description = "Itinerary status", example = "DRAFT/CREATED")
  private ItineraryStatusEnum itineraryStatus;

  @Schema(description = "Active status of the itinerary", example = "true/false")
  private Boolean isActive;
}
