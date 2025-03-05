/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.item.persistence.domain;

import com.nextuple.common.domain.DomainBaseEntity;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDomainDto extends DomainBaseEntity implements Serializable {

  private static final long serialVersionUID = -2283340988615096985L;
  private String itemId;
  private String itemSource;
  private String orgId;
  private String uom;
  private String vendorType;
  private Boolean isDSVEligible;
  private String product;
  private String color;
  private String size;
  private Boolean shipEligible;
  private Boolean parcelShipmentEligible;
  private Boolean pickEligible;
  private Map<String, Boolean> serviceOptionEligibilities;
  private Boolean shipAlone;
  private Double height;
  private Double width;
  private Double length;
  private Double volume;
  private String dimensionUom;
  private String volumeUom;
  private Double weight;
  private String weightUom;
  private Double processingTime;
  private String cost;
  private Boolean isHazmat;
  private String shortDescription;
  private Boolean isWhiteGlove;
  private Long leadTime;
  private String departmentNumber;
  private String departmentName;
  private String imageUrl;
  private Map<String, List<String>> inventoryNodeTypes;
  private String itemBanner;
  private String handlingType;
  private Integer buyingCost;
  private Double validDropoffDuration;
}
