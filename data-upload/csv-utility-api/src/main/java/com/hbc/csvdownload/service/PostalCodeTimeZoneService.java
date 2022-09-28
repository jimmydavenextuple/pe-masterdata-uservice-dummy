package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
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
    BaseResponse<List<String>> response =
        postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(orgId, state);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("FSA does not exist for given orgId and destination region / state");
      throw new PostalCodeTimezoneServiceException(
          "FSA does not exist for given orgId and destination region / state", orgId, state);
    }
  }
}
