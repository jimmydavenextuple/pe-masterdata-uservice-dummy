package com.nextuple.pe.masterdata.service;

import com.nextuple.pe.masterdata.domain.TransitDomain;
import com.nextuple.pe.masterdata.domain.entity.TransitEntity;
import com.nextuple.pe.masterdata.domain.inbound.TransitDataCreationRequest;
import com.nextuple.pe.masterdata.domain.inbound.TransitDataUpdationRequest;
import com.nextuple.pe.masterdata.domain.mapper.TransitMapper;
import com.nextuple.pe.masterdata.domain.outbound.TransitResponse;
import com.nextuple.pe.masterdata.error.FieldError;
import com.nextuple.pe.masterdata.exception.TransitDomainException;
import com.nextuple.pe.masterdata.exception.common.CommonServiceException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TransitService {

  private static final Logger logger = LoggerFactory.getLogger(TransitService.class);
  private static final String SOURCE_GEOZONE = "sourceGeozone";
  private static final String DESTINATION_GEOZONE = "destinationGeozone";
  private static final String ORG_ID = "orgId";
  private static final String CARRIER_SERVICE_ID = "carrierServiceId";

  private final TransitDomain transitDomain;

  public static final TransitMapper INSTANCE = Mappers.getMapper(TransitMapper.class);

  private static final String TRANSIT_EXCEPTION_MESSAGE =
      "Transit data not found with given details";

  public TransitResponse addTransitInfo(TransitDataCreationRequest transitDataCreationRequest)
      throws TransitDomainException {

    TransitEntity transitEntity = INSTANCE.toTransitEntity(transitDataCreationRequest);

    return INSTANCE.toTransitResponse(transitDomain.saveTransitEntity(transitEntity));
  }

  public TransitResponse updateTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      TransitDataUpdationRequest transitDataUpdationRequest)
      throws TransitDomainException, CommonServiceException {

    Optional<TransitEntity> existingTransitEntity =
        transitDomain.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    if (existingTransitEntity.isEmpty()) {
      logger.info(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    INSTANCE.updateTransitEntity(transitDataUpdationRequest, existingTransitEntity.get());
    return INSTANCE.toTransitResponse(transitDomain.saveTransitEntity(existingTransitEntity.get()));
  }

  public TransitResponse getTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      String serviceOption)
      throws TransitDomainException, CommonServiceException {

    List<TransitEntity> transitEntities =
        transitDomain.filterAndGetTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId, serviceOption);

    TransitEntity transitEntity = new TransitEntity();

    if (!transitEntities.isEmpty() && transitEntities.size() > 1) {
      if (carrierServiceId.equals("ALL")) {
        transitEntity =
            transitEntities.stream()
                .filter(x -> !"ALL".equals(x.getCarrierServiceId()))
                .findAny()
                .orElse(new TransitEntity());
      } else {
        transitEntity =
            transitEntities.stream()
                .filter(x -> carrierServiceId.equals(x.getCarrierServiceId()))
                .findAny()
                .orElse(
                    transitEntities.stream()
                        .filter(x -> !"ALL".equals(x.getCarrierServiceId()))
                        .findAny()
                        .orElse(new TransitEntity()));
      }
    } else if (transitEntities.size() == 1) {
      transitEntity = transitEntities.get(0);
    }

    if (transitEntities.isEmpty()) {
      logger.info(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    return INSTANCE.toTransitResponse(transitEntity);
  }

  public TransitResponse deleteTransitDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws TransitDomainException, CommonServiceException {
    Optional<TransitEntity> transitEntity =
        transitDomain.findTransitDetails(
            orgId, sourceGeozone, destinationGeozone, carrierServiceId);

    if (transitEntity.isEmpty()) {
      logger.info(TRANSIT_EXCEPTION_MESSAGE);
      Map<String, FieldError> errorMap = new HashMap<>();
      errorMap.put(ORG_ID, FieldError.builder().rejectedValue(orgId).build());
      errorMap.put(SOURCE_GEOZONE, FieldError.builder().rejectedValue(sourceGeozone).build());
      errorMap.put(
          DESTINATION_GEOZONE, FieldError.builder().rejectedValue(destinationGeozone).build());
      errorMap.put(
          CARRIER_SERVICE_ID, FieldError.builder().rejectedValue(carrierServiceId).build());
      throw new CommonServiceException(
          TRANSIT_EXCEPTION_MESSAGE, HttpStatus.NOT_FOUND, 0x1771, errorMap);
    }

    TransitResponse transitResponse = INSTANCE.toTransitResponse(transitEntity.get());
    transitDomain.deleteTransitDetails(transitEntity.get());
    return transitResponse;
  }
}
