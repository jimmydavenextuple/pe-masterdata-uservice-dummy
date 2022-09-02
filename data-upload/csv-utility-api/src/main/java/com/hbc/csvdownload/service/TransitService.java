package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitService {

  private final TransitFeign transitFeign;

  private final Logger logger = LoggerFactory.getLogger(TransitService.class);

  public List<TransitResponse> getTransitDetails(
      String orgId, String carrierServiceId, List<String> destinationGeoZones)
      throws TransitServiceException {
    logger.debug("Processing get FSA list for orgId and city");
    try {
      BaseResponse<List<TransitResponse>> response =
          transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(
              orgId, carrierServiceId, destinationGeoZones);
      if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
        return response.getPayload();
      } else {
        logger.error(
            "Transit details does not exist for given orgId, carrierServiceId and destinationGeoZones");
        throw new TransitServiceException(
            "Transit details does not exist for given orgId, carrierServiceId and destinationGeoZones",
            orgId,
            carrierServiceId);
      }
    } catch (FeignException e) {
      logger.error("Feign exception when fetching list of FSAs");
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new TransitServiceException(errorResponse.getMessage(), orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error("Exception while fetching list of FSAs");
      throw new TransitServiceException(
          "Exception while fetching list of FSAs", e, orgId, carrierServiceId);
    }
  }
}
