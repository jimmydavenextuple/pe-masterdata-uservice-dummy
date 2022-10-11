package com.hbc.dataupload.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.hbc.dataupload.util.TestUtil;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketRegionServiceTest {

  @InjectMocks private MarketRegionService marketRegionService;

  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getMarketRegions_Test() {
    when(postalCodeTimezoneFeign.getMarketRegionsForOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegionInfo());

    Assertions.assertDoesNotThrow(() -> marketRegionService.getMarketRegions(TestUtil.ORG_ID));
  }
}
