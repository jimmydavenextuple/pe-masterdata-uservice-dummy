package com.nextuple.item.domain.inbound;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Min;
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

  @Min(value = 0, message = "height can't be negative")
  private Double height;

  @Min(value = 0, message = "width can't be negative")
  private Double width;

  @Min(value = 0, message = "length can't be negative")
  private Double length;

  @Min(value = 0, message = "volume can't be negative")
  private Double volume;

  private String dimensionUom;
  private String volumeUom;

  @Min(value = 0, message = "weight can't be negative")
  private Double weight;

  private String weightUom;

  @Min(value = 0, message = "processingTime can't be negative")
  private Double processingTime;

  private String cost;
  private Boolean isHazmat;
  private String shortDescription;
  private Boolean isWhiteGlove;

  @Min(value = 0, message = "leadTime can't be negative")
  private Long leadTime;

  private String departmentNumber;
  private String departmentName;
  private String imageUrl;
  private Map<String, List<String>> inventoryNodeTypes;
  private String itemBanner;
}
