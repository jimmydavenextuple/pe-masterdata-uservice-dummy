/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.domain.inbound;

import com.nextuple.common.pojo.AdditionalAttributes;
import com.nextuple.common.validation.ValidationGroups;
import com.nextuple.node.domain.NodeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NodeBaseRequest extends AdditionalAttributes implements Serializable {

  @Schema(description = NodeConstants.STREET, example = NodeConstants.STREET_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "street can't be blank", groups = ValidationGroups.Create.class)
  private String street;

  @Schema(description = NodeConstants.CITY, example = NodeConstants.CITY_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "city can't be blank", groups = ValidationGroups.Create.class)
  private String city;

  @Schema(description = NodeConstants.TIMEZONE, example = NodeConstants.TIMEZONE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "timezone can't be blank", groups = ValidationGroups.Create.class)
  private String timezone;

  @Schema(
      description = NodeConstants.SERVICE_OPTION_ELGIBILITIES,
      example = NodeConstants.SERVICE_OPTION_ELGIBILITIES_EXAMPLE)
  @NotNull(
      message = "serviceOptionEligibilities can't be null",
      groups = ValidationGroups.Create.class)
  private Map<String, Boolean> serviceOptionEligibilities;

  @Schema(description = NodeConstants.SHIP_TO_HOME, example = NodeConstants.BOOL_EXAMPLE)
  @NotNull(message = "shipToHome can't be null", groups = ValidationGroups.Create.class)
  private Boolean shipToHome;

  @Schema(description = NodeConstants.BOPIS_ELIGIBLE, example = NodeConstants.BOOL_EXAMPLE)
  @NotNull(message = "bopisEligible can't be null", groups = ValidationGroups.Create.class)
  private Boolean bopisEligible;

  @Schema(description = NodeConstants.NODE_TYPE, example = NodeConstants.NODE_TYPE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "nodeType can't be blank", groups = ValidationGroups.Create.class)
  private String nodeType;

  @Schema(
      description = NodeConstants.NODE_LABOUR_TIER,
      example = NodeConstants.NODE_LABOUR_TIER_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "nodeLabourTier can't be blank", groups = ValidationGroups.Create.class)
  private String nodeLabourTier;

  @Schema(description = NodeConstants.STATE, example = NodeConstants.STATE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "state can't be blank", groups = ValidationGroups.Create.class)
  private String state;

  @Schema(description = NodeConstants.ZIP_CODE, example = NodeConstants.ZIP_CODE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "zipCode can't be blank", groups = ValidationGroups.Create.class)
  private String zipCode;

  @Schema(description = NodeConstants.COUNTRY, example = NodeConstants.COUNTRY_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "country can't be blank", groups = ValidationGroups.Create.class)
  private String country;

  @Schema(description = NodeConstants.LATITUDE, example = NodeConstants.LATITUDE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "latitude can't be blank", groups = ValidationGroups.Create.class)
  private String latitude;

  @Schema(description = NodeConstants.LONGITUDE, example = NodeConstants.LONGITUDE_EXAMPLE)
  @Length(max = 50)
  @NotBlank(message = "longitude can't be blank", groups = ValidationGroups.Create.class)
  private String longitude;

  @Schema(
      description = NodeConstants.START_WORKING_TIME,
      example = NodeConstants.START_WORKING_TIME_EXAMPLE)
  @Length(max = 50)
  private String startWorkingTime;

  @Schema(
      description = NodeConstants.LAST_WORKING_TIME,
      example = NodeConstants.LAST_WORKING_TIME_EXAMPLE)
  @Length(max = 50)
  private String lastWorkingTime;

  @Schema(description = NodeConstants.IS_ACTIVE, example = NodeConstants.BOOL_EXAMPLE)
  @NotNull(message = "isActive can't be null", groups = ValidationGroups.Create.class)
  private Boolean isActive;
}
