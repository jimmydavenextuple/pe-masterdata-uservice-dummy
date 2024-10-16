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
import java.util.Date;
import java.util.List;
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
public class SampleSourcingRequestForFormulaValidation implements Serializable {
  private static final long serialVersionUID = 8605544513031902777L;

  @Schema(description = "Unique identifier of the organization.", example = "NEXTUPLE_GR")
  @AttributePath(path = "/orgId")
  private String orgId;

  @Schema(
      description =
          "Service option of the order line. This will override the service option specified at the parent level.",
      example = "SDND / NEXTDAY / EXPRESS")
  @AttributePath(path = "/serviceOption")
  private String serviceOption;

  @Schema(description = "Fulfilment type of the order.", example = "SFS / STH etc.")
  @AttributePath(path = "/fulfillmentType")
  private String fulfillmentType;

  @Schema(description = "Date on which the order was placed.", example = "2023-02-07")
  @AttributePath(path = "/orderDate")
  private String orderDate;

  @Schema(description = "Unique identifier of the order.", example = "NXT-7744")
  @AttributePath(path = "/orderNo")
  private String orderNo;

  @Schema(description = "Unique identifier of the cart", example = "CART-01")
  @AttributePath(path = "/cartId")
  private String cartId;

  @Schema(
      description = "Name of the page from which the request originates",
      example = "PDP/Checkout/Cart")
  @AttributePath(path = "/pageName")
  private String pageName;

  @Schema(
      description = "Type of the order. Order type could be BOPIS or MARKETPLACE.",
      example = "BOPIS")
  @AttributePath(path = "/orderType")
  private String orderType;

  @Schema(description = "Destination details of the shipment.")
  @AttributePath(path = "/shipDestinationDetails")
  ShipDestinationDetails shipDestinationDetails;

  @Schema(
      description =
          "Estimated delivery date which was promised to the customer. This can be input from the Promising Engine.",
      example = "2023-02-07")
  @AttributePath(path = "/estimatedDeliveryDate")
  private Date estimatedDeliveryDate;

  @Schema(
      description = "Unique identifier of carrier service to be used for transit.",
      example = "FEDEX_GROUND")
  @AttributePath(path = "/carrierServiceId")
  private String carrierServiceId;

  @Schema(
      description =
          "Flag to indicate if an attempt should be made to reserve inventory for product lines during order creation.",
      example = "Y/N")
  @AttributePath(path = "/reserveInventoryFlag")
  private String reserveInventoryFlag;

  @Schema(
      description =
          "Flag to indicate if an attempt should be made to reserve capacity for product lines during order creation.",
      example = "Y/N")
  @AttributePath(path = "/reserveCapacity")
  private String reserveCapacity;

  @Schema(
      description =
          "Name of the node from where an item should be shipped in case of BOPIS fulfillment type.")
  @AttributePath(path = "/shipNode")
  private String shipNode;

  @Schema(
      description =
          "Details of custom attributes in the key-value pair such as Is Hazmat and item Classification. ")
  @AttributePath(path = "/customAttributes/")
  private Map<String, Object> customAttributes;

  @Schema(description = "List of order lines.")
  @AttributePath(path = "/orderLines/*")
  private List<SourcingOrderLines> orderLines;

  @Schema(description = "Unique identifier of session")
  @AttributePath(path = "/sessionId")
  private String sessionId;
}
