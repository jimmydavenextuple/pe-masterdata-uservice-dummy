package com.nextuple.pe.webhook.domain.dtos;

import com.nextuple.common.pojo.AdditionalAttributes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Item extends AdditionalAttributes {
  @NotBlank(message = "itemId can't be blank")
  private String itemId;

  private String itemSource;
  private String orgId;

  @NotBlank(message = "uom can't be blank")
  private String uom;

  private Boolean isDSVEligible;
  private String product;
  private String color;
  private String size;
  private Boolean shipAlone;

  @NotNull(message = "shipEligible can't be null")
  private Boolean shipEligible;

  @NotNull(message = "parcelShipmentEligible can't be null")
  private Boolean parcelShipmentEligible;

  @NotNull(message = "pickEligible can't be null")
  private Boolean pickEligible;

  @NotNull(message = "isWhiteGlove can't be null")
  private Boolean isWhiteGlove;

  private Double height;
  private Double width;
  private Double length;
  private Double volume;
  private String dimensionUom;
  private String volumeUom;
  private Double weight;
  private String weightUOM;
  private Long processingTime;
  private String cost;
  private Boolean isHazmat;
  private String shortDescription;
  private String departmentNumber;
  private String departmentName;
  private String imageUrl;
  private DateTime lastModifiedDate;

  @NotNull(message = "serviceOptionEligibilities can't be null")
  private Map<String, Boolean> serviceOptionEligibilities;

  private Map<String, List<String>> inventoryNodeTypes;

  private String itemBanner;

  @NotBlank(message = "handlingType can't be blank")
  private String handlingType;

  private Integer buyingCost;
  private Double validDropoffDuration;
}
