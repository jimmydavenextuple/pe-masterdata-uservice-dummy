package com.hbc.item.domain.entity;

import com.hbc.common.base.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(ItemPK.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "item")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ItemEntity extends BaseEntity {

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

  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb", name = "inventory_node_types")
  private Map<String, List<String>> inventoryNodeTypes;
}
