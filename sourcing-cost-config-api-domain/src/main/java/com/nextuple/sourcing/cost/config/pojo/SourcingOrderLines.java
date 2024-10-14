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
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourcingOrderLines implements Serializable {
  private static final long serialVersionUID = -6172058436253596392L;

  @Schema(
      description = "Quantity of the item that is required to be fulfilled in this order.",
      example = "2")
  @AttributePath(path = "/orderedQuantity")
  public Double orderedQuantity;

  @Schema(
      description =
          "Name of the node from where an item should be shipped in case of BOPIS fulfillment type.",
      example = "node123")
  @AttributePath(path = "/shipNode")
  public String shipNode;

  @Schema(description = "Unique identifier for the given order line.", example = "1")
  @AttributePath(path = "/lineId")
  public String lineId;

  @Schema(
      description = "Reservation ID that is made against the order line quantity to be fulfilled.",
      example = "RES_INV_123")
  @AttributePath(path = "/reservationId")
  public String reservationId;

  @Schema(description = "Unique identifier of carrier service to be used for transit.")
  @AttributePath(path = "/carrierServiceId")
  public String carrierServiceId;

  @Schema(description = "Destination details of the shipment.")
  @AttributePath(path = "/shipDestinationDetails")
  public ShipDestinationDetails shipDestinationDetails;

  @Schema(
      description =
          "Service option of the order line. This will override the service option specified at the parent level.",
      example = "SDND / NEXTDAY / EXPRESS")
  @AttributePath(path = "/serviceOption")
  private String serviceOption;

  @Schema(description = "Item details of the order line")
  @AttributePath(path = "/item")
  public SourcingItem item;

  @Schema(
      description =
          "Details of custom attributes in the key-value pair such as Is Hazmat and item Classification.")
  @AttributePath(path = "/customAttributes/")
  public Map<String, Object> customAttributes;
}
