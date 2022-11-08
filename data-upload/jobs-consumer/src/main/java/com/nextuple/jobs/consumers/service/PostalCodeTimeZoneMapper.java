package com.nextuple.jobs.consumers.service;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CREATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.DELETE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.UPDATE;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.domain.mapper.PostalCodeTimezoneRequestMapper;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.jobs.framework.common.domain.pojo.RecordInputDto;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimeZoneMapper implements FeignClientMapper {

  private final Logger logger = LoggerFactory.getLogger(PostalCodeTimeZoneMapper.class);
  private final PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @Setter private JobTypeEnum jobTypeEnum;

  public static final PostalCodeTimezoneRequestMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneRequestMapper.class);

  @Override
  public ModuleEnum getModule() {
    return ModuleEnum.POSTAL_CODE_TIMEZONE;
  }

  @Override
  public void setJobType(JobTypeEnum jobType) {
    this.jobTypeEnum = jobType;
  }

  @Override
  public Object getDTOFromCustomMapper(String stringRecord) {
    return null;
  }

  @Override
  public Map<String, String> getColumnNameMapping(String[] headerColumns) {
    return null;
  }

  @Override
  public Class mapTODto() {
    return PostalCodeTimezoneUpload.class;
  }

  @Override
  public ResponseEntity<?> callApi(Object request, RecordInputDto inputs) {
    var postalCodeTimezoneUpload = (PostalCodeTimezoneUpload) request;
    String action = postalCodeTimezoneUpload.getAction();
    switch (action) {
      case CREATE:
        return ResponseEntity.ok(
            postalCodeTimezoneFeign.createPostalCodeTimezone(
                INSTANCE.convertToCreatePostalCodeTimezoneRequest(postalCodeTimezoneUpload)));
      case UPDATE:
        return ResponseEntity.ok(
            postalCodeTimezoneFeign.updatePostalCodeTimezone(
                postalCodeTimezoneUpload.getOrgId(),
                postalCodeTimezoneUpload.getPostalCodePrefix(),
                INSTANCE.convertToUpdatePostalCodeTimezoneRequest(postalCodeTimezoneUpload)));
      case DELETE:
        return ResponseEntity.ok(
            postalCodeTimezoneFeign.deletePostalCodeTimezone(
                postalCodeTimezoneUpload.getOrgId(),
                postalCodeTimezoneUpload.getPostalCodePrefix()));
      default:
        {
          logger.error("Invalid action type: {}", postalCodeTimezoneUpload.getAction());
          throw new CsvDataValidationException(
              "Please provide the valid action: " + postalCodeTimezoneUpload.getAction());
        }
    }
  }
}
