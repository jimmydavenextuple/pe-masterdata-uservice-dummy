package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.jobs.framework.common.utils.ExceptionUtils;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import feign.FeignException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class PostalCodeTimeZoneService {

  private final PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  private final Logger logger = LoggerFactory.getLogger(PostalCodeTimeZoneService.class);

  public List<String> getFSAsByOrgIdAndState(String orgId, String state)
      throws PostalCodeTimezoneServiceException {
    logger.debug("Processing get FSA list for orgId and state");
    try {
      BaseResponse<List<String>> response =
          postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(orgId, state);
      if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
        return response.getPayload();
      } else {
        logger.error("FSA does not exist for given orgId and destination region / state");
        throw new PostalCodeTimezoneServiceException(
            "FSA does not exist for given orgId and destination region / state", orgId, state);
      }
    } catch (FeignException e) {
      logger.error("Feign exception when fetching list of FSAs");
      var errorResponse = ExceptionUtils.parseFeignException(e);
      throw new PostalCodeTimezoneServiceException(errorResponse.getMessage(), orgId, state);
    } catch (Exception e) {
      logger.error("Exception while fetching list of FSAs");
      throw new PostalCodeTimezoneServiceException(
          "Exception while fetching list of FSAs", e, orgId, state);
    }
  }
}
