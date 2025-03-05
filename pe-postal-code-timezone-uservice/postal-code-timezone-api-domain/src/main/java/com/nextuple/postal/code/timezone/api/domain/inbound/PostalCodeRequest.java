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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PostalCodeRequest extends AdditionalAttributes {
  @NotBlank(message = "orgId can't be empty")
  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE")
  private String orgId;

  @NotBlank(message = "zipCode can't be blank")
  @Schema(description = "Zip code of the source or destination node.", example = "T2PS2K")
  private String zipCode;

  @NotBlank(message = "zipCodePrefix can't be empty")
  @Schema(description = "First three characters of the zip code.", example = "T2P")
  private String zipCodePrefix;

  @NotBlank(message = "country can't be empty")
  @Schema(description = "Country of the zip code.", example = "CA")
  private String country;

  @NotBlank(message = "state can't be empty")
  @Schema(description = "State of the zip code.", example = "Toronto")
  private String state;

  @NotBlank(message = "city can't be empty")
  @Schema(description = "City of the zip code.", example = "Ontario")
  private String city;

  @NotBlank(message = "latitude can't be empty")
  @Schema(description = "Latitude of the zip code.", example = "23.21313")
  private String latitude;

  @NotBlank(message = "longitude can't be empty")
  @Schema(description = "Longitude of the zip code.", example = "74.12132")
  private String longitude;

  @NotBlank(message = "timeZone can't be empty")
  @Schema(description = "Timezone of the zip code.", example = "America/Toronto")
  private String timeZone;
}
