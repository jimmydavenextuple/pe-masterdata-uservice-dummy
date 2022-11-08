package com.nextuple.postal.code.timezone.api.domain.projection;

public interface MarketRegionProjection {

  String getCountry();

  long getNoOfStates();

  long getNoOfCities();

  long getNoOfPostalCodePrefixes();

  String getUploadDate();

  void setUploadDate(String v);
}
