package com.nextuple.pe.masterdata.calendar.util;

import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalendarValidation {
  @Autowired CarrierFeign carrierFeign;
  @Autowired NodeFeign nodeFeign;

  private static final Logger logger = LoggerFactory.getLogger(CalendarValidation.class);

  private static final String ORG_ID = "orgId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  public void validateNodeId(String nodeId, String orgId) throws CommonServiceException {
    try {
      BaseResponse<NodeResponse> nodeResponse = nodeFeign.getNodeDetails(nodeId, orgId);

      if (!nodeResponse.isSuccess()
          || Objects.isNull(nodeResponse.getPayload())
          || Boolean.FALSE.equals(nodeResponse.getPayload().getIsActive())) {
        throw new CommonServiceException("", HttpStatus.BAD_REQUEST, 0x1772, new HashMap<>());
      }
    } catch (Exception e) {
      logger.error("Cannot create the calendar as Node id is invalid or node is inactive");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          "Cannot create the calendar as Node id is invalid or node is inactive",
          HttpStatus.BAD_REQUEST,
          0x1780,
          errorMap);
    }
  }

  public boolean isValidCarrierServiceId(String orgId, String carrierServiceId) {
    BaseResponse<List<CarrierServiceResponse>> response =
        carrierFeign.getCarrierServiceListByOrgId(orgId);
    var isValidId = false;
    for (CarrierServiceResponse carrierServiceResponse : response.getPayload()) {
      if (carrierServiceResponse.getCarrierServiceId().equals(carrierServiceId)) {
        isValidId = true;
        break;
      }
    }
    return isValidId;
  }

  public void validateCarrierServiceId(String orgId, String carrierServiceId)
      throws CommonServiceException {
    if (Boolean.FALSE.equals(isValidCarrierServiceId(orgId, carrierServiceId))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          "Cannot create the calendar as carrier service id is invalid",
          HttpStatus.BAD_REQUEST,
          0x1781,
          errorMap);
    }
  }
}
