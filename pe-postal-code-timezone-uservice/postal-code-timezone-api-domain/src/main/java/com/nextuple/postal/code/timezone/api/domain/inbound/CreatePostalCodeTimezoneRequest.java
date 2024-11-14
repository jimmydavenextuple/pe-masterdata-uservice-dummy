/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.inbound;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostalCodeTimezoneRequest implements Serializable {
  private static final long serialVersionUID = 768362509382042475L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  @NotBlank(message = "orgId can't be blank")
  private String orgId;

  @Schema(
      description = "First three characters of the zip code of the source or destination node.",
      example = "T2P")
  @NotBlank(message = "zipCodePrefix can't be blank")
  private String zipCodePrefix;

  @Schema(description = "Timezone of the source or destination node.", example = "EST")
  @NotBlank(message = "timeZone can't be blank")
  private String timeZone;

  @Schema(description = "Country of the source or destination node.", example = "US")
  @NotBlank(message = "country can't be blank")
  private String country;

  @Schema(description = "City of the source or destination node.", example = "New york")
  @NotBlank(message = "city can't be blank")
  private String city;

  @Schema(description = "State of the source or destination node.", example = "NY")
  @NotBlank(message = "state can't be blank")
  private String state;

  @Schema(description = "Longitude of the source or destination node.", example = "97.3")
  @NotBlank(message = "longitude can't be blank")
  private String longitude;

  @Schema(description = "Latitude of the source or destination node.", example = "90.3")
  @NotBlank(message = "latitude can't be blank")
  private String latitude;
}
