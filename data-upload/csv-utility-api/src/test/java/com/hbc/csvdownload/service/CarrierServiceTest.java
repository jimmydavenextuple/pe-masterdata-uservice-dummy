package com.hbc.csvdownload.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.CarrierServiceException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {
  @Mock private CarrierFeign carrierFeign;

  @InjectMocks private CarrierService carrierService;

  @InjectMocks private TestUtil testUtil;

  @Test
  void getCarrierServiceTest() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString()))
        .thenReturn(
            BaseResponse.builder()
                .payload(List.of(testUtil.getCarrierServiceCalendarResponse()))
                .build());

    Assertions.assertDoesNotThrow(() -> carrierService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }

  @Test
  void getCarrierServiceTestEmptyResponse() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString()))
        .thenReturn(BaseResponse.builder().payload(new ArrayList<>()).build());

    Assertions.assertThrows(
        CarrierServiceException.class, () -> carrierService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }

  @Test
  void getCarrierServiceTestNullResponse() {
    when(carrierFeign.getCarrierServiceListByOrgId(anyString())).thenReturn(null);

    Assertions.assertThrows(
        CarrierServiceException.class, () -> carrierService.getCarrierService(TestUtil.ORG_ID));

    verify(carrierFeign, times(1)).getCarrierServiceListByOrgId(anyString());
  }
}
