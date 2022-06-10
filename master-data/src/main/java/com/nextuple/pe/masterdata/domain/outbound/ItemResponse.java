package com.nextuple.pe.masterdata.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse implements Serializable {

  private String itemId;
  private String itemSource;
  private String orgId;
  private String uom;
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
  private Long processingTime;
  private String cost;
  private Boolean isHazmat;
  private Long leadTime;
  private Boolean isWhiteGlove;
}
