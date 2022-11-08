package com.nextuple.dataupload.service;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import com.nextuple.postgres.config.ReaderDS;
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
  public List<MarketRegionInfo> getMarketRegions(String orgId) {
    BaseResponse<List<MarketRegionInfo>> result =
        postalCodeTimezoneFeign.getMarketRegionsForOrgId(orgId);
    return result.getPayload();
  }
}
