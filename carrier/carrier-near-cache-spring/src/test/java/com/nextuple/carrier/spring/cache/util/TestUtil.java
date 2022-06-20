package com.nextuple.carrier.spring.cache.util;

import com.nextuple.carrier.cache.domain.CarrierCacheKey;
import com.nextuple.carrier.cache.domain.CarrierCacheValue;
import com.nextuple.carrier.cache.domain.CarrierDetails;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.response.BaseResponse;

public class TestUtil {

  public static final String ORG_ID = "Org_Id_01";
  public static final String CARRIER_ID = "Carrier_Id_01";
  public static final String CARRIER_SERVICE_ID = "Service_Id_01";
  public static final String CARRIER_NAME = "Carrier-01";
  public static final String SERVICE_NAME = "Service-01";
  public static final String SERVICE_OPTIONS = "Standard";

  private CarrierDetails getCarrierDetails() {
    return CarrierDetails.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public CarrierCacheValue getCarrierCacheValue() {
    CarrierDetails carrierDetails = getCarrierDetails();
    return CarrierCacheValue.builder().carrierDetails(carrierDetails).build();
  }

  private CarrierServiceResponse getCarrierResponse() {
    return CarrierServiceResponse.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .carrierName(CARRIER_NAME)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTIONS)
        .build();
  }

  public BaseResponse<CarrierServiceResponse> getBaseResponseOfCarrierResponse() {
    return BaseResponse.builder()
        .message("Carrier details fetched successfully")
        .payload(getCarrierResponse())
        .build();
  }

  public CarrierCacheKey getCarrierCacheKey() {
    return CarrierCacheKey.builder()
        .carrierId(CARRIER_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .orgId(ORG_ID)
        .build();
  }
}
