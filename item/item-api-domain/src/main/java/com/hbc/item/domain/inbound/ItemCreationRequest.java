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

  @NotBlank(message = "itemId can't be blank")
  @Length(max = 50)
  private String itemId;

  @Length(max = 50)
  private String itemSource;

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "uom can't be blank")
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

  @NotNull(message = "shipEligible can't be null")
  private Boolean shipEligible;

  @NotNull(message = "parcelShipmentEligible can't be null")
  private Boolean parcelShipmentEligible;

  @NotNull(message = "bopisEligible can't be null")
  private Boolean bopisEligible;

  @NotNull(message = "serviceOptionEligibilities can't be null")
  private Map<String, Boolean> serviceOptionEligibilities;

  @NotNull(message = "shipAlone can't be null")
  private Boolean shipAlone;

  @Min(value = 0, message = "Height can't be negative")
  private Double height;

  @Min(value = 0, message = "width can't be negative")
  private Double width;

  @Min(value = 0, message = "length can't be negative")
  private Double length;

  @Min(value = 0, message = "volume can't be negative")
  private Double volume;

  @Length(max = 50)
  private String dimensionUom;

  @Length(max = 50)
  private String volumeUom;

  private Double weight;

  @Length(max = 50)
  private String weightUom;

  @NotNull
  @Min(value = 0, message = "processingTime can't be negative")
  private Double processingTime;
  @NotNull(message = "processingTime can't be null")
  private Double processingTime;

  @Length(max = 50)
  private String cost;

  private Boolean isHazmat;

  @Length(max = 200)
  private String shortDescription;

  @NotNull(message = "isWhiteGlove can't be null")
  private Boolean isWhiteGlove;

  private Long leadTime;

  @Length(max = 50)
  private String departmentNumber;

  @Length(max = 50)
  private String departmentName;

  private String imageUrl;
}
