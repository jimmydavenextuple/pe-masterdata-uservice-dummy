/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.consumer.dto;

import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import com.nextuple.node.domain.NodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NodeFeedDto extends CommonMasterDataFieldsDto implements Serializable {

  private static final long serialVersionUID = 1240891589171888066L;

  @Schema(description = NodeConstants.NODE_ID, example = NodeConstants.NODE_ID_EXAMPLE)
  private String nodeId;

  @Schema(description = NodeConstants.STREET, example = NodeConstants.STREET_EXAMPLE)
  private String street;

  @Schema(description = NodeConstants.CITY, example = NodeConstants.CITY_EXAMPLE)
  private String city;

  @Schema(description = NodeConstants.STATE, example = NodeConstants.STATE_EXAMPLE)
  private String state;

  @Schema(description = NodeConstants.ZIP_CODE, example = NodeConstants.ZIP_CODE_EXAMPLE)
  private String zipCode;

  @Schema(description = NodeConstants.COUNTRY, example = NodeConstants.COUNTRY_EXAMPLE)
  private String country;

  @Schema(description = NodeConstants.LATITUDE, example = NodeConstants.LATITUDE_EXAMPLE)
  private String latitude;

  @Schema(description = NodeConstants.LONGITUDE, example = NodeConstants.LONGITUDE_EXAMPLE)
  private String longitude;

  @Schema(description = NodeConstants.TIMEZONE, example = NodeConstants.TIMEZONE_EXAMPLE)
  private String timezone;

  @Schema(
      description = NodeConstants.START_WORKING_TIME,
      example = NodeConstants.START_WORKING_TIME_EXAMPLE)
  private String startWorkingTime;

  @Schema(
      description = NodeConstants.LAST_WORKING_TIME,
      example = NodeConstants.LAST_WORKING_TIME_EXAMPLE)
  private String lastWorkingTime;

  @Schema(
      description = NodeConstants.SERVICE_OPTION_ELGIBILITIES,
      example = NodeConstants.SERVICE_OPTION_ELGIBILITIES_EXAMPLE)
  private Map<String, Boolean> serviceOptionEligibilities;

  @Schema(description = NodeConstants.SHIP_TO_HOME, example = NodeConstants.BOOL_EXAMPLE)
  private Boolean shipToHome;

  @Schema(description = NodeConstants.BOPIS_ELIGIBLE, example = NodeConstants.BOOL_EXAMPLE)
  @NotNull(message = "bopisEligible can't be null")
  private Boolean bopisEligible;

  @Schema(description = NodeConstants.NODE_TYPE, example = NodeConstants.NODE_TYPE_EXAMPLE)
  private String nodeType;

  @Schema(
      description = NodeConstants.NODE_LABOUR_TIER,
      example = NodeConstants.NODE_LABOUR_TIER_EXAMPLE)
  private String nodeLabourTier;

  @Schema(description = NodeConstants.IS_ACTIVE, example = NodeConstants.BOOL_EXAMPLE)
  private Boolean isActive;
}
