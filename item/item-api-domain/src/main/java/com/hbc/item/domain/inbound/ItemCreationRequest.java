package com.hbc.item.domain.inbound;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemCreationRequest implements Serializable {

  private static final long serialVersionUID = 1905122041950251207L;

  @NotBlank(message = "itemId can't be blank")
  private String itemId;

  private String itemSource;

  @NotBlank(message = "orgId can't be blank")
  private String orgId;

  @NotBlank(message = "uom can't be blank")
  private String uom;

  private String vendorType;

  private Boolean isDSVEligible;

  private String product;

  private String color;

  private String size;

  @NotNull(message = "shipEligible can't be null")
  private Boolean shipEligible;

  @NotNull(message = "parcelShipmentEligible can't be null")
  private Boolean parcelShipmentEligible;

  @NotNull(message = "bopisEligible can't be null")
  private Boolean bopisEligible;

  @NotNull(message = "serviceOptionEligibilities can't be null")
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

  @NotNull(message = "isWhiteGlove can't be null")
  private Boolean isWhiteGlove;

  private Long leadTime;

  private String departmentNumber;

  private String departmentName;

  private String imageUrl;

  private DateTime lastModifiedDate;
}
