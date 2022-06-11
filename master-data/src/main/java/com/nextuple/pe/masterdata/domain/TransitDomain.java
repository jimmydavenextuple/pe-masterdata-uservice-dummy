package com.nextuple.pe.masterdata.domain;

import com.nextuple.pe.masterdata.domain.entity.TransitEntity;
import com.nextuple.pe.masterdata.domain.repository.TransitRepository;
import com.nextuple.pe.masterdata.exception.TransitDomainException;
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
    List<TransitEntity> transitEntities;
    try {

      if ("ALL".equals(carrierServiceId)) {
        transitEntities =
            transitRepository.findByCarrierServiceIdWithServiceOption(
                orgId, sourceGeozone, destinationGeozone, "ALL-" + serviceOption, carrierServiceId);
      } else {
        transitEntities =
            transitRepository.findByCarrierServiceIdsWithServiceOption(
                orgId, sourceGeozone, destinationGeozone, carrierServiceId, "ALL-" + serviceOption);
      }
      return transitEntities;
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
}
