/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */
package com.nextuple.item.persistence.entity;

import com.nextuple.item.persistence.entity.key.ItemKey;
import com.nextuple.postgres.entity.CommonBaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(ItemKey.class)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Table(name = "item")
public class ItemEntity extends CommonBaseEntity {

  @Id
  @Column(name = "item_id")
  private String itemId;

  @Column(name = "item_source")
  private String itemSource;

  @Id
  @Column(name = "org_id")
  private String orgId;

  @Id
  @Column(name = "uom")
  private String uom;

  @Column(name = "vendor_type")
  private String vendorType;

  @Column(name = "is_dsv_eligible")
  private Boolean isDSVEligible;

  @Column(name = "product")
  private String product;

  @Column(name = "color")
  private String color;

  @Column(name = "size")
  private String size;

  @Column(name = "ship_eligible")
  private Boolean shipEligible;

  @Column(name = "parcel_shipment_eligible")
  private Boolean parcelShipmentEligible;

  @Column(name = "pick_eligible")
  private Boolean pickEligible;

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Boolean> serviceOptionEligibilities;

  @Column(name = "ship_alone")
  private Boolean shipAlone;

  @Column(name = "height")
  private Double height;

  @Column(name = "width")
  private Double width;

  @Column(name = "length")
  private Double length;

  @Column(name = "volume")
  private Double volume;

  @Column(name = "dimension_uom")
  private String dimensionUom;

  @Column(name = "volume_uom")
  private String volumeUom;

  @Column(name = "weight")
  private Double weight;

  @Column(name = "weight_uom")
  private String weightUom;

  @Column(name = "processing_time")
  private Double processingTime;

  @Column(name = "cost")
  private String cost;

  @Column(name = "is_hazmat")
  private Boolean isHazmat;

  @Column(name = "short_description")
  private String shortDescription;

  @Column(name = "is_white_glove")
  private Boolean isWhiteGlove;

  @Column(name = "lead_time")
  private Long leadTime;

  @Column(name = "department_number")
  private String departmentNumber;

  @Column(name = "department_name")
  private String departmentName;

  @Column(name = "image_url")
  private String imageUrl;

  @Type(JsonBinaryType.class)
  @Column(columnDefinition = "jsonb", name = "inventory_node_types")
  private Map<String, List<String>> inventoryNodeTypes;

  @Column(name = "item_banner")
  private String itemBanner;

  @Column(name = "handling_type")
  private String handlingType;

  @Column(name = "buying_cost")
  private Integer buyingCost;
}
