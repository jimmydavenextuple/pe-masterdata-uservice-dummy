/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketRegionInfo {
  @Schema(description = "Country of the zip code.", example = "CN")
  private String country;

  @Schema(description = "Number of the states in the region.", example = "4")
  private long noOfStates;

  @Schema(description = "Number of the cities in the region.", example = "4")
  private long noOfCities;

  @Schema(description = "Number of the zip code prefixes in the region.", example = "4")
  private long noOfZipCodePrefixes;

  @Schema(description = "Upload date of the region.", example = "2023-01-01")
  private String uploadDate;
}
