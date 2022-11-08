package com.nextuple.dataupload.service;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.common.outbound.TransitBufferDetailsResponse;
import com.nextuple.transit.domain.feign.TransitBufferConfigRequestFeign;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
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

  public PagePayload<TransitBufferDetailsResponse> getTransitTimeBufferDetailsForCarrierServices(
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

  public List<TransitBufferConfigResponse> getTransitBufferConfigRequests(
      String orgId, String carrierServiceId) {
    BaseResponse<List<TransitBufferConfigResponse>> response =
        transitBufferConfigRequestFeign.getTransitBufferConfigRequests(orgId, carrierServiceId);
    return response.getPayload();
  }
}
