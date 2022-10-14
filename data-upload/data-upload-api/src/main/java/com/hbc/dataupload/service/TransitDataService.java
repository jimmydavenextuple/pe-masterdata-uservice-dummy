package com.hbc.dataupload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.common.response.error.FieldError;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.DistinctGeozonesResponse;
import feign.FeignException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitDataService {

  private final Logger logger = LoggerFactory.getLogger(TransitDataService.class);
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private final TransitFeign transitFeign;

  public DistinctGeozonesResponse getDistinctGeozonesList(String orgId, String carrierServiceId)
      throws CommonServiceException {
    try {
      BaseResponse<DistinctGeozonesResponse> destinationGeozones =
          transitFeign.getDistinctSourceAndDestinationGeozones(orgId, carrierServiceId);
      if (CollectionUtils.isEmpty(destinationGeozones.getPayload().getSourceGeozones())) {
        logger.error(
            "List of source geozones not available for given orgId: {} and carrierServiceId: {}",
            orgId,
            carrierServiceId);
        throw new CommonServiceException(
            String.format(
                "List of source geozones not available for given orgId: %s and carrierServiceId: %s",
                orgId, carrierServiceId),
            HttpStatus.BAD_REQUEST,
            0x1778,
            Map.of(
                ORG_ID,
                FieldError.builder().rejectedValue(orgId).build(),
                CARRIER_SERVICE_ID,
                FieldError.builder().rejectedValue(carrierServiceId).build()));
      }
      if (CollectionUtils.isEmpty(destinationGeozones.getPayload().getDestinationGeozones())) {
        logger.error(
            "List of destination geozones not available for given orgId: {} and carrierServiceId: {}",
            orgId,
            carrierServiceId);
        throw new CommonServiceException(
            String.format(
                "List of destination geozones not available for given orgId: %s and carrierServiceId: %s",
                orgId, carrierServiceId),
            HttpStatus.BAD_REQUEST,
            0x1778,
            Map.of(
                ORG_ID,
                FieldError.builder().rejectedValue(orgId).build(),
                CARRIER_SERVICE_ID,
                FieldError.builder().rejectedValue(carrierServiceId).build()));
      }
      return destinationGeozones.getPayload();
    } catch (FeignException e) {
      logger.error("Feign exception while fetching distinct source and destination geozones", e);
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new CommonServiceException(
          errorResponse.getMessage(),
          e,
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    } catch (Exception e) {
      logger.error("Error while fetching distinct source and destination geozones", e);
      throw new CommonServiceException(
          e.getMessage(),
          e,
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of(
              ORG_ID,
              FieldError.builder().rejectedValue(orgId).build(),
              CARRIER_SERVICE_ID,
              FieldError.builder().rejectedValue(carrierServiceId).build()));
    }
  }
}
