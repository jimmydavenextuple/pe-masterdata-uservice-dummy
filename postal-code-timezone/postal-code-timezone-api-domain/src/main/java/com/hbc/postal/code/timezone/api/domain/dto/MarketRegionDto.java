package com.hbc.postal.code.timezone.api.domain.dto;

import java.util.Date;

public interface MarketRegionDto {

  String getCountry();

  long getNoOfStates();

  long getNoOfCities();

  long getNoOfPostalCodePrefixes();

  Date getUploadDate();

  void setUploadDate(String v);
}
