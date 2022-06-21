package com.nextuple.item.domain.events;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemMasterEvent implements Serializable {

  private static final long serialVersionUID = 1935122041650251277L;

  private String itemId;
  private String itemSource;
  private String orgId;
  private String uom;
  private String vendorType;
  private String product;
  private String color;
  private String size;
  private Boolean shipAlone;
  private Boolean shipEligible;
  private Boolean parcelShipmentEligible;
  private Boolean expressEligible;
  private Boolean sdndEligible;
  private Boolean bopisEligible;
  private Boolean isWhiteGlove;
  private Double height;
  private Double width;
  private Double length;
  private Double volume;
  private String dimensionUom;
  private String volumeUom;
  private Double weight;
  private String weightUom;
  private Long processingTime;
  private String cost;
  private Boolean isHazmat;
  private String shortDescription;
  private String departmentNumber;
  private String departmentName;
  private String imageUrl;
}