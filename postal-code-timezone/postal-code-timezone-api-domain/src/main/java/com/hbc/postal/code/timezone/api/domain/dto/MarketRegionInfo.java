package com.hbc.postal.code.timezone.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketRegionInfo {
  private String country;
  private long noOfStates;
  private long noOfCities;
  private long noOfPostalCodePrefixes;
  private String uploadDate;
}
