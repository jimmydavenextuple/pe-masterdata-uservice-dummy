package com.hbc.item.domain.inbound;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemCreationRequest implements Serializable {

  private static final long serialVersionUID = 1905122041950251207L;

  @NotBlank
  @Length(max = 50)
  private String itemId;

  @Length(max = 50)
  private String itemSource;

  @NotBlank
  @Length(max = 50)
  private String orgId;

  @NotBlank
  @Length(max = 50)
  private String uom;

  @Length(max = 50)
  private String vendorType;

  @Length(max = 50)
  private String product;

  @Length(max = 50)
  private String color;

  @Length(max = 50)
  private String size;

  @NotNull private Boolean shipEligible;

  @NotNull private Boolean parcelShipmentEligible;

  @NotNull private Boolean bopisEligible;

  @NotNull private Map<String, Boolean> serviceOptionEligibilities;

  @NotNull private Boolean shipAlone;

  private Double height;
  private Double width;
  private Double length;
  private Double volume;

  @Length(max = 50)
  private String dimensionUom;

  @Length(max = 50)
  private String volumeUom;

  private Double weight;

  @Length(max = 50)
  private String weightUom;

  @NotNull private Double processingTime;

  @Length(max = 50)
  private String cost;

  private Boolean isHazmat;

  @Length(max = 200)
  private String shortDescription;

  @NotNull private Boolean isWhiteGlove;

  private Long leadTime;

  @Length(max = 50)
  private String departmentNumber;

  @Length(max = 50)
  private String departmentName;

  private String imageUrl;
}
