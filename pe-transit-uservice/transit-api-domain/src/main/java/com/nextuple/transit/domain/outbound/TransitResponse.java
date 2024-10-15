/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.outbound;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitResponse implements Serializable {

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @Schema(description = "Source geo zone of the transit.", example = "H1R")
  private String sourceGeozone;

  @Schema(description = "Destination geo zone of the transit.", example = "A1B")
  private String destinationGeozone;

  @Schema(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
  private String carrierServiceId;

  @Schema(description = "Days required for the transit.", example = "2.1")
  private Float transitDays;

  @Schema(description = "Buffer days for the transit.", example = "3.1")
  private Double bufferDays;

  @Schema(
      description = "The start date of the transit buffer in UTC format.",
      example = "2023-12-24T11:58:22Z")
  private Date bufferStartDate;

  @Schema(
      description = "The end date of the transit buffer in UTC format.",
      example = "2023-12-26T11:58:22Z")
  private Date bufferEndDate;
}
