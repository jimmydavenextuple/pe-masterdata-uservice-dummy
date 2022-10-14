package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.TransitDetailsRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
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
    var transitDetailsRequest = new TransitDetailsRequest();
    transitDetailsRequest.setDestinationGeozones(destinationGeoZones);
    BaseResponse<List<TransitResponse>> response =
        transitFeign.getTransitTimeDetailsForDestinationGeoZonesList(
            orgId, carrierServiceId, transitDetailsRequest);
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
  }

  public TransitTimeEntriesDto getTransitTimeEntries(String orgId, String carrierServiceId)
      throws TransitServiceException {
    try {
      BaseResponse<TransitTimeEntriesDto> response =
          transitFeign.getTransitTimeEntries(orgId, carrierServiceId);
      return response.getPayload();
    } catch (Exception e) {
      throw new TransitServiceException(
          "Transit details does not exist for given orgId and carrierServiceId  ",
          orgId,
          carrierServiceId);
    }
  }
}
