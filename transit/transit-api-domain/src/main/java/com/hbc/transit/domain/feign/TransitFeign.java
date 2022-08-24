package com.hbc.transit.domain.feign;

import com.hbc.common.response.BaseResponse;
import com.hbc.transit.domain.dto.TransitTimeEntriesDto;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import com.hbc.transit.domain.inbound.TransitDataUpdationRequest;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "pe-config-transit",
    url = "${spring.application.dependencies.transit:http://pe-config-transit:8080/}")
public interface TransitFeign {

  @PostMapping("/transit")
  BaseResponse<TransitResponse> addTransitData(
      @RequestBody TransitDataCreationRequest transitDataCreationRequest);

  @PutMapping("/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<TransitResponse> updateTransitData(
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "sourceGeozone") String sourceGeozone,
      @PathVariable(name = "destinationGeozone") String destinationGeozone,
      @PathVariable(name = "carrierServiceId") String carrierServiceId,
      @RequestBody TransitDataUpdationRequest transitDataUpdationRequest);

  @GetMapping(
      "/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}/{serviceOption}")
  BaseResponse<TransitResponse> getTransitDetails(
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "sourceGeozone") String sourceGeozone,
      @PathVariable(name = "destinationGeozone") String destinationGeozone,
      @PathVariable(name = "carrierServiceId") String carrierServiceId,
      @PathVariable(name = "serviceOption") String serviceOption);

  @DeleteMapping("/transit/{orgId}/{sourceGeozone}/{destinationGeozone}/{carrierServiceId}")
  BaseResponse<TransitResponse> deleteTransitDetails(
      @PathVariable(name = "orgId") String orgId,
      @PathVariable(name = "sourceGeozone") String sourceGeozone,
      @PathVariable(name = "destinationGeozone") String destinationGeozone,
      @PathVariable(name = "carrierServiceId") String carrierServiceId);

  @GetMapping("/transit/{orgId}/{destinationGeozone}/")
  BaseResponse<List<TransitResponse>> getTransitDetailsList(
      @PathVariable String orgId,
      @PathVariable String destinationGeozone,
      @RequestParam List<String> sourceGeozones);

  @GetMapping("/transit/transit-entries/{orgId}/{carrierServiceId}")
  BaseResponse<TransitTimeEntriesDto> getTransitTimeEntries(
      @PathVariable String orgId, @PathVariable String carrierServiceId);

  @GetMapping("/transit/{orgId}/{destinationGeozone}/")
  BaseResponse<List<TransitResponse>> getTransitDetailsListWithoutSourcingNodes(
      @PathVariable String orgId, @PathVariable String destinationGeozone);
}
