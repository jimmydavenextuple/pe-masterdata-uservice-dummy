package com.hbc.csvdownload.service;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.CarrierServiceException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CarrierService {

  private final CarrierFeign carrierFeign;

  private final Logger logger = LoggerFactory.getLogger(CarrierService.class);

  public List<CarrierServiceResponse> getCarrierService(String orgId)
      throws CarrierServiceException {
    logger.debug("Processing get Carrier service by orgId");

    BaseResponse<List<CarrierServiceResponse>> response =
        carrierFeign.getCarrierServiceListByOrgId(orgId);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("Carrier Service does not exist for given orgId");
      throw new CarrierServiceException("Carrier Service does not exist for given orgId", orgId);
    }
  }
}
