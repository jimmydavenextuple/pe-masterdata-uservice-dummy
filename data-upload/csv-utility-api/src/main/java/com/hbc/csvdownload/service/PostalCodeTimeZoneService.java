package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
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
          "FSA does not exist for given orgId and destination region / state", orgId, state, null);
    }
  }

  public List<PostalCodeTimezoneDto> getPostalCodeTimeZoneByOrgIdAndCountry(
      String orgId, String country) throws PostalCodeTimezoneServiceException {
    logger.debug("Processing get postal code timezone for orgId and country");
    BaseResponse<List<PostalCodeTimezoneDto>> response =
        postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(orgId, country);
    if (response != null && !CollectionUtils.isEmpty(response.getPayload())) {
      return response.getPayload();
    } else {
      logger.error("Market Regions does not exist for given orgId and country");
      throw new PostalCodeTimezoneServiceException(
          "Market Regions does not exist for given orgId and country", orgId, null, country);
    }
  }
}
