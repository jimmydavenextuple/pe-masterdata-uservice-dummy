/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class UpdatePostalCodeTimezoneRequest extends AdditionalAttributes implements Serializable {
  private static final long serialVersionUID = 9006684261651646439L;

  @Schema(description = "Country of the source or destination node.", example = "US")
  @NotBlank(message = "country can't be empty.")
  private String country;

  @Schema(description = "State of the source or destination node.", example = "NY")
  @NotBlank(message = "state can't be empty.")
  private String state;

  @Schema(description = "City of the source or destination node.", example = "New york")
  @NotBlank(message = "city can't be empty.")
  private String city;

  @Schema(description = "Latitude of the source or destination node.", example = "97.3")
  @NotBlank(message = "latitude can't be empty.")
  private String latitude;

  @Schema(description = "Longitude of the source or destination node.", example = "97.3")
  @NotBlank(message = "longitude can't be empty.")
  private String longitude;

  @Schema(description = "Timezone of the source or destination node.", example = "EST")
  @NotBlank(message = "timeZone can't be empty.")
  private String timeZone;
}
