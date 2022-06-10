package com.nextuple.pe.masterdata.domain.entity;

import com.nextuple.pe.masterdata.domain.primaryKeys.ItemId;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Entity
@IdClass(ItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "item")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ItemEntity {

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

  @Column(name = "bopis_eligilble")
  private Boolean bopisEligible;

  @Type(type = "jsonb")
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
  private Long processingTime;

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
}
