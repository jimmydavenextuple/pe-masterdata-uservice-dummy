package com.hbc.dataupload.service;

import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.base.PagePayload;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.hbc.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.hbc.transit.domain.outbound.TransitBufferConfigResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class TransitTimeBufferService {

  private final CarrierFeign carrierFeign;
  private final TransitBufferConfigRequestFeign transitBufferConfigRequestFeign;

  public PagePayload<TransitBufferDetailsResponse> getTransitTimeBufferDetails(
      String orgId, int pageNo, int pageSize, String sortBy, String sortOrder) {
    BaseResponse<PagePayload<CarrierServiceResponse>> response =
        carrierFeign.getCarrierServiceListWithPagination(
            orgId, pageNo, pageSize, sortBy, sortOrder);

    List<TransitBufferDetailsResponse> transitBufferDetailsResponses =
        formListOfTransitBufferDetailsResponseObjects(response.getPayload().getData());

    PagePayload<TransitBufferDetailsResponse> payload = new PagePayload<>();
    payload.setPagination(response.getPayload().getPagination());
    payload.setData(transitBufferDetailsResponses);
    return payload;
  }

  private List<TransitBufferDetailsResponse> formListOfTransitBufferDetailsResponseObjects(
      List<CarrierServiceResponse> carrierServiceResponseList) {
    if (!CollectionUtils.isEmpty(carrierServiceResponseList)) {
      return carrierServiceResponseList.stream()
          .map(
              carrierServiceResponse -> {
                var carrierServiceId = carrierServiceResponse.getCarrierServiceId();
                var orgId = carrierServiceResponse.getOrgId();
                BaseResponse<List<TransitBufferConfigResponse>> response =
                    transitBufferConfigRequestFeign.getTransitBufferConfigRequests(
                        orgId, carrierServiceId);
                return TransitBufferDetailsResponse.builder()
                    .carrierServiceId(carrierServiceId)
                    .orgId(orgId)
                    .hasTransitBuffer(!CollectionUtils.isEmpty(response.getPayload()))
                    .build();
              })
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }
}
