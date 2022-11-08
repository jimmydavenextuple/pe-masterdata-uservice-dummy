package com.nextuple.pe.masterdata.calendar.service;

import com.nextuple.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.nextuple.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.nextuple.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.nextuple.carrier.domain.feign.CarrierFeign;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.error.FieldError;
import com.nextuple.node.domain.feign.NodeFeign;
import com.nextuple.node.domain.outbound.NodeResponse;
import com.nextuple.pe.masterdata.calendar.domain.CalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDomain;
import com.nextuple.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.nextuple.pe.masterdata.calendar.domain.mapper.CalendarMapper;
import com.nextuple.pe.masterdata.calendar.domain.repository.NodeCarrierServiceCalendarRepository;
import com.nextuple.pe.masterdata.calendar.exception.CalendarDomainException;
import com.nextuple.pe.masterdata.calendar.exception.DateException;
import com.nextuple.pe.masterdata.calendar.util.DateValidation;
import com.nextuple.postgres.config.ReaderDS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class NodeCarrierServiceCalendarService {

  private static final Logger logger =
      LoggerFactory.getLogger(NodeCarrierServiceCalendarService.class);
  private static final CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
  private final NodeCarrierServiceCalendarDomain nodeCarrierServiceCalendarDomain;
  private final CalendarDomain calendarDomain;
  private final NodeCarrierServiceCalendarRepository nodeCarrierServiceCalendarRepository;
  private final DateValidation dateValidation;
  private static final String ORG_ID = "orgId";
  private static final String CALENDAR_ID = "calendarId";
  private static final String NODE_ID = "nodeId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";
  private static final String EFFECTIVE_DATE = "effectiveDate";

  @Autowired NodeFeign nodeFeign;
  @Autowired CarrierFeign carrierFeign;

  /** Creates a new Node Carrier Service Calendar */
  public NodeCarrierServiceCalendarResponse processCreateNodeCarrierServiceCalendarResponse(
      NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest)
      throws CalendarDomainException, DateException, CommonServiceException {
    if (!dateValidation.validateDate(nodeCarrierServiceCalendarRequest.getEffectiveDate())) {
      throw new DateException(
          "Invalid Date",
          nodeCarrierServiceCalendarRequest.getCalendarId(),
          nodeCarrierServiceCalendarRequest.getOrgId());
    }
    validateCalendarId(
        nodeCarrierServiceCalendarRequest.getCalendarId(),
        nodeCarrierServiceCalendarRequest.getOrgId());

    validateNodeId(
        nodeCarrierServiceCalendarRequest.getNodeId(),
        nodeCarrierServiceCalendarRequest.getOrgId());
    if (Boolean.FALSE.equals(
        validateCarrierServiceId(
            nodeCarrierServiceCalendarRequest.getOrgId(),
            nodeCarrierServiceCalendarRequest.getCarrierServiceId()))) {
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(nodeCarrierServiceCalendarRequest.getOrgId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarRequest.getCarrierServiceId())
              .build());
      throw new CommonServiceException(
          "Cannot create a node carrier service calendar as carrier service id is invalid",
          HttpStatus.BAD_REQUEST,
          0x1773,
          errorMap);
    }
    var nodeCarrierServiceCalendarEntity =
        INSTANCE.convertToNodeCarrierServiceCalendarEntity(nodeCarrierServiceCalendarRequest);
    Optional<NodeCarrierServiceCalendarEntity> existingNodeCarrierServiceCalendarEntity =
        nodeCarrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                nodeCarrierServiceCalendarRequest.getCalendarId(),
                nodeCarrierServiceCalendarRequest.getOrgId(),
                nodeCarrierServiceCalendarRequest.getNodeId(),
                nodeCarrierServiceCalendarRequest.getCarrierServiceId(),
                nodeCarrierServiceCalendarRequest.getEffectiveDate());
    if (existingNodeCarrierServiceCalendarEntity.isPresent()) {
      logger.error(
          "Node Carrier Service Calendar already exists for calendarId:{}, orgId:{}, nodeId:{}, carrierServiceId:{}, effectiveDate:{}",
          nodeCarrierServiceCalendarEntity.getCalendarId(),
          nodeCarrierServiceCalendarEntity.getOrgId(),
          nodeCarrierServiceCalendarEntity.getNodeId(),
          nodeCarrierServiceCalendarEntity.getCarrierServiceId(),
          nodeCarrierServiceCalendarEntity.getEffectiveDate());
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(
          CALENDAR_ID,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarEntity.getCalendarId())
              .build());
      errorMap.put(
          ORG_ID,
          FieldError.builder().rejectedValue(nodeCarrierServiceCalendarEntity.getOrgId()).build());
      errorMap.put(
          NODE_ID,
          FieldError.builder().rejectedValue(nodeCarrierServiceCalendarEntity.getNodeId()).build());
      errorMap.put(
          CARRIER_SERVICE_ID,
          FieldError.builder().rejectedValue(nodeCarrierServiceCalendarEntity.getNodeId()).build());
      errorMap.put(
          EFFECTIVE_DATE,
          FieldError.builder()
              .rejectedValue(nodeCarrierServiceCalendarEntity.getEffectiveDate())
              .build());
      throw new CommonServiceException(
          "Node Carrier Service Calendar already exists for the given details",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
    var savedNodeCarrierServiceCalendarEntity =
        nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(
            nodeCarrierServiceCalendarEntity);
    return INSTANCE.convertToNodeCarrierServiceCalendarResponse(
        savedNodeCarrierServiceCalendarEntity);
  }

  /** Get Node, Carrier&Service Calendar details by orgId, nodeId and carrierServiceId */
  @ReaderDS
  public List<NodeCarrierServiceCalendarResponse> processGetNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    return INSTANCE.convertToNodeCarrierServiceCalendarResponseList(
        getAndFilterNodeCarrierServiceCalendar(orgId, nodeId, carrierServiceId, serviceOption));
  }

  public List<NodeCarrierServiceCalendarEntity> getAndFilterNodeCarrierServiceCalendar(
      String orgId, String nodeId, String carrierServiceId, Optional<String> serviceOption)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> entityList;
    List<NodeCarrierServiceCalendarEntity> filteredList;
    try {
      if (serviceOption.isPresent() && !ObjectUtils.isEmpty(serviceOption.get())) {
        String carrierServiceOption = "ALL-" + serviceOption.get();
        entityList =
            nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId, carrierServiceOption);
        filteredList =
            "ALL".equals(carrierServiceId)
                ? new ArrayList<>()
                : getFilteredEntityList(carrierServiceId, entityList);
        if (CollectionUtils.isEmpty(filteredList)) {
          filteredList = getFilteredEntityList(carrierServiceOption, entityList);
        }
      } else {
        entityList =
            nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(
                orgId, nodeId, carrierServiceId);
        filteredList = getFilteredEntityList(carrierServiceId, entityList);
      }
      return CollectionUtils.isEmpty(filteredList) ? entityList : filteredList;
    } catch (Exception e) {
      logger.error("Error in getAndFilterNodeCarrierServiceCalendar");
      throw e;
    }
  }

  private List<NodeCarrierServiceCalendarEntity> getFilteredEntityList(
      String carrierServiceId, List<NodeCarrierServiceCalendarEntity> entityList) {
    return entityList.stream()
        .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
        .collect(Collectors.toList());
  }

  public void validateCalendarId(String calendarId, String orgId)
      throws CalendarDomainException, CommonServiceException {
    var calendarEntity = calendarDomain.getCalendar(orgId, calendarId);

    if (ObjectUtils.isEmpty(calendarEntity)) {
      logger.error("Cannot create a node carrier service calendar as calendarId/orgId is invalid");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(CALENDAR_ID, FieldError.builder().rejectedValue(calendarId).build());
      throw new CommonServiceException(
          "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
          HttpStatus.NOT_FOUND,
          0x1771,
          errorMap);
    }
  }

  public void validateNodeId(String nodeId, String orgId) throws CommonServiceException {
    try {
      BaseResponse<NodeResponse> nodeResponse = nodeFeign.getNodeDetails(nodeId, orgId);

      if (!nodeResponse.isSuccess()
          || Objects.isNull(nodeResponse.getPayload())
          || Boolean.FALSE.equals(nodeResponse.getPayload().getIsActive())) {
        throw new CommonServiceException("", HttpStatus.BAD_REQUEST, 0x1772, new HashMap<>());
      }
    } catch (Exception e) {
      logger.error(
          "Cannot create a node carrier service calendar as Node id is invalid or node is inactive");
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(NODE_ID, FieldError.builder().rejectedValue(nodeId).build());
      throw new CommonServiceException(
          "Cannot create a node carrier service calendar as Node id is invalid or node is inactive",
          HttpStatus.BAD_REQUEST,
          0x1772,
          errorMap);
    }
  }

  public List<NodeCarrierCalendarCacheKeyDto> getAllNodeCarrierCalendarCacheKeys(Integer limit)
      throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        nodeCarrierServiceCalendarDomain.getAllNodeCarrierServiceCalendars(limit);

    return INSTANCE.convertToNodeCarrierCalendarCacheKeyDtoList(nodeCarrierServiceCalendarEntities);
  }

  public boolean validateCarrierServiceId(String orgId, String carrierServiceId) {
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
}
