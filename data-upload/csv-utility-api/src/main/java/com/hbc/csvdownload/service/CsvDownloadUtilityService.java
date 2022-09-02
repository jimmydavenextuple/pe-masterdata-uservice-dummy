package com.hbc.csvdownload.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.csvdownload.exception.CsvDownloadUtilityServiceException;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.csvdownload.exception.TransitServiceException;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class CsvDownloadUtilityService {

  private final Logger logger = LoggerFactory.getLogger(CsvDownloadUtilityService.class);

  private final PostalCodeTimeZoneService postalCodeTimeZoneService;
  private final TransitService transitService;

  public String downloadTransitTimesForSourceAndDestinationRegion(
      String orgId, String carrierServiceId, String sourceRegion, String destinationRegion)
      throws PostalCodeTimezoneServiceException, TransitServiceException,
          CsvDownloadUtilityServiceException {
    logger.debug("Processing download transit times for source and destination regions");

    try {
      List<String> destinationFsaList =
          postalCodeTimeZoneService.getFsaList(orgId, destinationRegion);
      Set<String> sourceFsaSet =
          new HashSet<>(postalCodeTimeZoneService.getFsaList(orgId, sourceRegion));
      List<TransitResponse> destinationFsaTransitResponses =
          transitService.getTransitDetails(orgId, carrierServiceId, destinationFsaList);
      List<TransitResponse> filteredTransitResponseList =
          destinationFsaTransitResponses.stream()
              .filter(transitResponse -> sourceFsaSet.contains(transitResponse.getSourceGeozone()))
              .collect(Collectors.toList());

      if (CollectionUtils.isEmpty(filteredTransitResponseList)) {
        logger.error("No transit details available for source and destination FSAs");
        throw new CsvDownloadUtilityServiceException(
            "No transit details available for source and destination FSAs", orgId);
      }

      Set<String> filteredSourceFsaSet =
          filteredTransitResponseList.stream()
              .map(TransitResponse::getSourceGeozone)
              .collect(Collectors.toSet());

      Map<String, Float> sourceAndTransitTimeMap = new TreeMap<>();
      filteredSourceFsaSet.forEach(fsa -> sourceAndTransitTimeMap.put(fsa, null));

      Map<String, Map<String, Float>> transitTimesDataMap = new HashMap<>();
      filteredTransitResponseList.forEach(
          transitResponse -> {
            if (CollectionUtils.isEmpty(
                transitTimesDataMap.get(transitResponse.getDestinationGeozone()))) {
              transitTimesDataMap.put(
                  transitResponse.getDestinationGeozone(), new HashMap<>(sourceAndTransitTimeMap));
            }
          });

      filteredTransitResponseList.forEach(
          transitResponse ->
              transitTimesDataMap
                  .get(transitResponse.getDestinationGeozone())
                  .put(transitResponse.getSourceGeozone(), transitResponse.getTransitDays()));

      String sourceFsaHeader = String.join(",", filteredSourceFsaSet);
      return constructCsvData(orgId, carrierServiceId, transitTimesDataMap, sourceFsaHeader);
    } catch (Exception e) {
      logger.error("Error while forming csv contents string");
      throw new CsvDownloadUtilityServiceException(
          "Error while forming csv contents string", e, orgId);
    }
  }

  private String constructCsvData(
      String orgId,
      String carrierServiceId,
      Map<String, Map<String, Float>> transitTimesDataMap,
      String sourceFsaHeader) {
    String csvContents =
        String.format(
            "orgId,%s%nCarrier Service:,%s%nDestination FSA / Source FSA ->,%s",
            orgId, carrierServiceId, sourceFsaHeader);
    String rows =
        transitTimesDataMap.keySet().stream()
            .map(
                destinationFsa -> {
                  var sourceFsaAndTransitTimesMap = transitTimesDataMap.get(destinationFsa);
                  String transitTimes =
                      sourceFsaAndTransitTimesMap.keySet().stream()
                          .map(
                              sourceFsa ->
                                  String.valueOf(sourceFsaAndTransitTimesMap.get(sourceFsa)))
                          .collect(Collectors.joining(","));
                  return String.join(",", destinationFsa, transitTimes);
                })
            .collect(Collectors.joining("\n"));
    return String.join("\n", csvContents, rows);
  }
}
