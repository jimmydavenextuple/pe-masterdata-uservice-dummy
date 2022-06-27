package com.hbc.item.domain.inbound;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemUpdationRequest implements Serializable {

  private String itemSource;
  private String vendorType;
  private String product;
  private String color;
  private String size;
  private Boolean shipEligible;
  private Boolean parcelShipmentEligible;
  private Boolean bopisEligible;
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
}
