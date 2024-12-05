/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ZoneRequest implements Serializable {
  private static final long serialVersionUID = -1750511939409558942L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @Schema(description = "Unique identifier of the carrier service.", example = "UPS-GROUND")
  @NotBlank(message = "carrierServiceId can't be blank")
  @Length(max = 50)
  private String carrierServiceId;

  @Schema(description = "Source geo zone of the transit.", example = "A1B")
  @NotBlank(message = "sourceGeozone can't be blank")
  @Length(max = 50)
  private String sourceGeozone;

  @Schema(description = "Destination geo zone of the transit.", example = "H1R")
  @NotBlank(message = "destinationGeozone can't be blank")
  @Length(max = 50)
  private String destinationGeozone;

  @Schema(
      description = "Geographical regions that help in determining the transit time for packages.",
      example = "Zone1")
  @NotBlank(message = "zone can't be blank")
  @Length(max = 20)
  private String zone;
}
