/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.domain.inbound;

import com.nextuple.item.domain.constants.ItemConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemUpdationRequest implements Serializable {

  private static final long serialVersionUID = -9137352122126908186L;

  @Schema(description = ItemConstants.ITEM_SOURCE, example = ItemConstants.ITEM_SOURCE_EXAMPLE)
  private String itemSource;

  @Schema(description = ItemConstants.VENDOR_TYPE, example = ItemConstants.VENDOR_TYPE_EXAMPLE)
  private String vendorType;

  @Schema(description = ItemConstants.PRODUCT, example = ItemConstants.PRODUCT_EXAMPLE)
  private String product;

  @Schema(description = ItemConstants.COLOR, example = ItemConstants.COLOR_EXAMPLE)
  private String color;

  @Schema(description = ItemConstants.SIZE, example = ItemConstants.SIZE_EXAMPLE)
  private String size;

  @Schema(description = ItemConstants.SHIP_ELIGIBLE, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean shipEligible;

  @Schema(
      description = ItemConstants.PARCEL_SHIPMENT_ELIGIBLE,
      example = ItemConstants.BOOL_EXAMPLE)
  private Boolean parcelShipmentEligible;

  @Schema(description = ItemConstants.PICK_ELIGIBLE, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean pickEligible;

  @Schema(
      description = ItemConstants.SERVICE_OPTION_ELIGIBILITIES,
      example = ItemConstants.SERVICE_OPTION_ELIGIBILITIES_EXAMPLE)
  private Map<String, Boolean> serviceOptionEligibilities;

  @Schema(description = ItemConstants.SHIP_ALONE, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean shipAlone;

  @Min(value = 0, message = "height can't be negative")
  @Schema(description = ItemConstants.HEIGHT, example = ItemConstants.DIMENSION_EXAMPLE)
  private Double height;

  @Min(value = 0, message = "width can't be negative")
  @Schema(description = ItemConstants.WIDTH, example = ItemConstants.DIMENSION_EXAMPLE)
  private Double width;

  @Min(value = 0, message = "length can't be negative")
  @Schema(description = ItemConstants.LENGTH, example = ItemConstants.DIMENSION_EXAMPLE)
  private Double length;

  @Min(value = 0, message = "volume can't be negative")
  @Schema(description = ItemConstants.VOLUME, example = ItemConstants.DIMENSION_EXAMPLE)
  private Double volume;

  @Schema(description = ItemConstants.DIMENSION_UOM, example = ItemConstants.DIMENSION_UOM_EXAMPLE)
  private String dimensionUom;

  @Schema(description = ItemConstants.VOLUME_UOM, example = ItemConstants.VOLUME_UOM_EXAMPLE)
  private String volumeUom;

  @Min(value = 0, message = "weight can't be negative")
  @Schema(description = ItemConstants.WEIGHT, example = ItemConstants.DIMENSION_EXAMPLE)
  private Double weight;

  @Schema(description = ItemConstants.WEIGHT_UOM, example = ItemConstants.WEIGHT_UOM_EXAMPLE)
  private String weightUom;

  @Schema(description = ItemConstants.PROCESSING_TIME, example = ItemConstants.TIME_EXAMPLE)
  private transient Optional<@Min(value = 0, message = "processingTime can't be negative") Double>
      processingTime;

  @Schema(description = ItemConstants.COST, example = ItemConstants.DIMENSION_EXAMPLE)
  private String cost;

  @Schema(description = ItemConstants.IS_HAZMAT, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean isHazmat;

  @Schema(
      description = ItemConstants.SHORT_DESCRIPTION,
      example = ItemConstants.ITEM_BANNER_EXAMPLE)
  private String shortDescription;

  @Schema(description = ItemConstants.IS_WHITE_GLOVE, example = ItemConstants.BOOL_EXAMPLE)
  private Boolean isWhiteGlove;

  @Min(value = 0, message = "leadTime can't be negative")
  @Schema(description = ItemConstants.LEAD_TIME, example = ItemConstants.TIME_EXAMPLE)
  private Long leadTime;

  @Schema(
      description = ItemConstants.DEPARTMENT_NUMBER,
      example = ItemConstants.DEPARTMENT_NUMBER_EXAMPLE)
  private String departmentNumber;

  @Schema(
      description = ItemConstants.DEPARTMENT_NAME,
      example = ItemConstants.DEPARTMENT_NAME_EXAMPLE)
  private String departmentName;

  @Schema(description = ItemConstants.IMAGE_URL, example = ItemConstants.IMAGE_URL_EXAMPLE)
  private String imageUrl;

  @Schema(description = ItemConstants.INVENTORY_NODE_TYPES)
  private Map<String, List<String>> inventoryNodeTypes;

  @Schema(description = ItemConstants.ITEM_BANNER, example = ItemConstants.ITEM_BANNER_EXAMPLE)
  private String itemBanner;

  @Schema(description = ItemConstants.HANDLING_TYPE, example = ItemConstants.HANDLING_TYPE_EXAMPLE)
  private String handlingType;

  @Schema(description = ItemConstants.BUYING_COST, example = ItemConstants.BUYING_COST_EXAMPLE)
  private Integer buyingCost;

  @Schema(description = ItemConstants.HOLDING_TIME, example = ItemConstants.HOLDING_TIME_EXAMPLE)
  private Double holdingTime;
}
