package com.hbc.jobs.consumers.service;

import com.hbc.jobs.consumers.exception.TransitMapperException;
import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.pojo.RecordInputDto;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.inbound.TransitDataCreationRequest;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransitMapper implements FeignClientMapper {

  private final TransitFeign transitFeign;

  public TransitMapper(TransitFeign transitFeign) {
    this.transitFeign = transitFeign;
  }

  @Setter private JobTypeEnum jobTypeEnum;

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return getDefaultColumnNameMapping(headerColumns);
  }

  @Override
  public Class mapTODto() throws TransitMapperException {
    try {
      if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
        return TransitDataCreationRequest.class;
      }
      log.error("Unable to map an object!");
      throw new TransitMapperException(
          "Error while mapping an object to the expected object", jobTypeEnum);
    } catch (Exception e) {
      log.error("Error while mapping to DTO");
      throw new TransitMapperException(
          "Exception while mapping an object to expected object", e, jobTypeEnum);
    }
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs)
      throws TransitMapperException {
    if (jobTypeEnum == JobTypeEnum.UPLOAD_TRANSIT_TIMES) {
      var transitDataCreationRequest = (TransitDataCreationRequest) request;
      return ResponseEntity.ok(transitFeign.addTransitData(transitDataCreationRequest));
    }
    log.error("Failed to make a call based on job type");
    throw new TransitMapperException("Please provide the valid job type", jobTypeEnum);
  }
}
