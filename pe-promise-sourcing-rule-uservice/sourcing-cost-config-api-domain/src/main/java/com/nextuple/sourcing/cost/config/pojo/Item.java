/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.sourcing.cost.config.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nextuple.sourcing.cost.config.customannotations.AttributePath;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

  private static final long serialVersionUID = -8903037622117038612L;

  @Schema(description = "Item id")
  @AttributePath(path = "/itemId")
  private String itemId;

  @Schema(description = "Unit of measurement for item")
  @AttributePath(path = "/uom")
  private String uom;

  @Schema(description = "Height of item")
  @AttributePath(path = "/height")
  private Double height;

  @Schema(description = "Width of item")
  @AttributePath(path = "/width")
  private Double width;

  @Schema(description = "Length of item")
  @AttributePath(path = "/length")
  private Double length;

  @Schema(description = "Unit of measurement for dimension of item")
  @AttributePath(path = "/dimensionUom")
  private String dimensionUom;

  @Schema(description = "Weight of item")
  @AttributePath(path = "/weight")
  private Double weight;

  @Schema(description = "Unit of measurement for Weight of item")
  @AttributePath(path = "/weightUom")
  private String weightUom;

  @Schema(description = "Processing time of item")
  @AttributePath(path = "/processingTime")
  private Long processingTime;

  @Schema(description = "Handling type of item")
  @AttributePath(path = "/handlingType")
  private String handlingType;

  @Schema(description = "Requested quantity of item")
  @AttributePath(path = "/requestedQuantity")
  private Double requestedQuantity;

  @Schema(description = "Fulfilled quantity of item")
  @AttributePath(path = "/fulfilledQuantity")
  private Double fulfilledQuantity;

  @Schema(description = "ATP of item")
  @AttributePath(path = "/atp")
  private Double atp;
}
