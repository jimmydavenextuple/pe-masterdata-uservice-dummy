package com.hbc.dataupload.service;

import com.hbc.postal.code.timezone.api.domain.dto.MarketRegionDto;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.hbc.postgres.config.ReaderDS;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketRegionService {

  private final PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @ReaderDS
  public List<MarketRegionDto> getMarketRegions(String orgId) {
    return postalCodeTimezoneFeign.getMarketRegionsForOrgId(orgId).getPayload();
  }
}
