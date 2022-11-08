package com.nextuple.dataupload.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nextuple.dataupload.service.MarketRegionService;
import com.nextuple.dataupload.util.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketRegionControllerTest {

  @Mock private MarketRegionService marketRegionService;

  @InjectMocks private MarketRegionController marketRegionController;
  @InjectMocks private TestUtil testUtil;

  @Test
  void getMarketRegions_Test() {
    when(marketRegionService.getMarketRegions(anyString()))
        .thenReturn(testUtil.getMarketRegionInfo().getPayload());

    Assertions.assertDoesNotThrow(() -> marketRegionController.getMarketRegions(TestUtil.ORG_ID));
  }
}
