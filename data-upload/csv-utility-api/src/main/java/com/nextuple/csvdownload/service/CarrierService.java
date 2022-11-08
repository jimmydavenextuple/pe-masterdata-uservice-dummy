package com.nextuple.csvdownload.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CarrierServiceException;
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
