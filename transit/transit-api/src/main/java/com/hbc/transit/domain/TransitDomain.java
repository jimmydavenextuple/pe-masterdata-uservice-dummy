package com.hbc.transit.domain;

import com.hbc.transit.domain.entity.TransitEntity;
import com.hbc.transit.exception.TransitDomainException;
import com.hbc.transit.repository.TransitRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitDomain {

  private static final Logger logger = LoggerFactory.getLogger(TransitDomain.class);

  private static final String ERROR_MESSAGE = "Error while fetching transit list";

  private final TransitRepository transitRepository;

  public TransitEntity saveTransitEntity(TransitEntity transitEntity)
      throws TransitDomainException {
    try {
      return transitRepository.save(transitEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to save transit data");
      throw new TransitDomainException(
          "Unable to save transit data",
          transitEntity.getOrgId(),
          transitEntity.getSourceGeozone(),
          transitEntity.getDestinationGeozone(),
          transitEntity.getCarrierServiceId());
    }
  }

  public List<TransitEntity> filterAndGetTransitDetails(
      String orgId,
      String sourceGeozone,
      String destinationGeozone,
      String carrierServiceId,
      String serviceOption)
      throws TransitDomainException {
    try {

      return transitRepository.findByCarrierServiceIdsWithServiceOption(
          orgId, sourceGeozone, destinationGeozone, carrierServiceId, serviceOption);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find transit details");
      throw new TransitDomainException(
          "Error while finding transit details",
          orgId,
          sourceGeozone,
          destinationGeozone,
          carrierServiceId);
    }
  }

  public Optional<TransitEntity> findTransitDetails(
      String orgId, String sourceGeozone, String destinationGeozone, String carrierServiceId)
      throws TransitDomainException {

    try {
      return transitRepository.findByOrgIdAndSourceGeozoneAndDestinationGeozoneAndCarrierServiceId(
          orgId, sourceGeozone, destinationGeozone, carrierServiceId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to find transit details");
      throw new TransitDomainException(
          "Error while finding transit details",
          orgId,
          sourceGeozone,
          destinationGeozone,
          carrierServiceId);
    }
  }

  public void deleteTransitDetails(TransitEntity transitEntity) throws TransitDomainException {
    try {
      transitRepository.delete(transitEntity);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to delete transit details");
      throw new TransitDomainException(
          "Error while deleting transit details",
          transitEntity.getOrgId(),
          transitEntity.getSourceGeozone(),
          transitEntity.getDestinationGeozone(),
          transitEntity.getCarrierServiceId());
    }
  }

  public List<TransitEntity> fetchTransitList(
      String orgId, String destinationGeozone, List<String> sourceGeozones)
      throws TransitDomainException {
    try {
      return transitRepository.findByOrgIdAndDestinationGeozoneAndSourceGeoZones(
          orgId, destinationGeozone, sourceGeozones);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit list");
      throw new TransitDomainException(ERROR_MESSAGE, orgId, null, destinationGeozone, null);
    }
  }

  public List<String> fetchDestinationGeozones(
      String orgId, String sourceGeozone, List<String> carrierServiceIds)
      throws TransitDomainException {
    try {
      return transitRepository.findByOrgIdAndSourceGeozoneAndCarrierServiceIds(
          orgId, sourceGeozone, carrierServiceIds);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit list");
      throw new TransitDomainException(ERROR_MESSAGE, orgId, sourceGeozone, null, null);
    }
  }

  public Integer fetchTransitEntitiesCount(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return transitRepository.findTransitCountByOrgIdAndCarrierServiceId(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(String.valueOf(e), "Unable to fetch transit entities count");
      throw new TransitDomainException(
          "Error while fetching transit entities count", orgId, null, null, carrierServiceId);
    }
  }

  public List<TransitEntity> fetchTransitListForDestinationGeoZone(
      String orgId, String destinationGeozone) throws TransitDomainException {
    try {
      return transitRepository.findByOrgIdAndDestinationGeozone(orgId, destinationGeozone);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozone: {}",
          orgId,
          destinationGeozone);
      throw new TransitDomainException(ERROR_MESSAGE, orgId, null, destinationGeozone, null);
    }
  }

  public List<TransitEntity> fetchTransitListForDestinationGeoZones(
      String orgId, String carrierServiceId, List<String> destinationGeozones)
      throws TransitDomainException {
    try {
      return transitRepository.findByOrgIdAndCarrierServiceIdAndDestinationGeozoneIn(
          orgId, carrierServiceId, destinationGeozones);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch transit list for orgId: {} and destination geozones",
          orgId);
      throw new TransitDomainException(
          "Error while fetching transit entity list", orgId, null, null, carrierServiceId);
    }
  }

  public List<String> fetchDistinctSourceGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return transitRepository.findDistinctSourceGeoZones(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch distinct source geozones list for orgId: {} and carrierServiceId: {}",
          orgId,
          carrierServiceId);
      throw new TransitDomainException(
          "Unable to fetch distinct source geozones list for orgId and carrierServiceId",
          orgId,
          null,
          null,
          carrierServiceId);
    }
  }

  public List<String> fetchDistinctDestinationGeoZones(String orgId, String carrierServiceId)
      throws TransitDomainException {
    try {
      return transitRepository.findDistinctDestinationGeoZones(orgId, carrierServiceId);
    } catch (Exception e) {
      logger.error(
          String.valueOf(e),
          "Unable to fetch distinct destination geozones list for orgId: {} and carrierServiceId: {}",
          orgId,
          carrierServiceId);
      throw new TransitDomainException(
          "Unable to fetch distinct destination geozones list for given orgId and carrierServiceId",
          orgId,
          null,
          null,
          carrierServiceId);
    }
  }
}
