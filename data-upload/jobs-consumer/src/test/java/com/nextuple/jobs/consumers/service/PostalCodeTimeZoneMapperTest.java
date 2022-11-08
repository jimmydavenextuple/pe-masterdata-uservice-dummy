package com.nextuple.jobs.consumers.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.exception.CsvDataValidationException;
import com.nextuple.jobs.consumers.common.TestUtil;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.pojo.PostalCodeTimezoneUpload;
import com.nextuple.jobs.framework.common.enums.ModuleEnum;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimeZoneMapperTest {

  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;
  @InjectMocks private PostalCodeTimeZoneMapper postalCodeTimeZoneMapper;
  @InjectMocks private TestUtil testUtil;

  @Test
  void mapTODto() {
    Class marketRegionMapper1 = postalCodeTimeZoneMapper.mapTODto();
    Assertions.assertEquals(PostalCodeTimezoneUpload.class, marketRegionMapper1);
  }

  @Test
  void getModule() {
    ModuleEnum moduleEnum = postalCodeTimeZoneMapper.getModule();
    Assertions.assertEquals(ModuleEnum.POSTAL_CODE_TIMEZONE, moduleEnum);
  }

  @Test
  void setJobType() {
    Assertions.assertDoesNotThrow(
        () -> postalCodeTimeZoneMapper.setJobType(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE));
  }

  @Test
  void getDTOFromCustomMapper() {
    Assertions.assertNull(postalCodeTimeZoneMapper.getDTOFromCustomMapper("request"));
  }

  @Test
  void getColumnNameMapping() {
    Assertions.assertNull(postalCodeTimeZoneMapper.getColumnNameMapping(new String[] {"request"}));
  }

  @Test
  void callApiCreatePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("CREATE");
    when(postalCodeTimezoneFeign.createPostalCodeTimezone(any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiUpdatePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("UPDATE");
    when(postalCodeTimezoneFeign.updatePostalCodeTimezone(any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiDeletePostalCodeTimezone() {
    Object object = testUtil.getPostalCodeTimezoneUpload("DELETE");
    when(postalCodeTimezoneFeign.deletePostalCodeTimezone(any(), any()))
        .thenReturn(BaseResponse.builder().payload(testUtil.getPostalCodeTimezoneDto()).build());

    ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> response =
        (ResponseEntity<BaseResponse<PostalCodeTimezoneDto>>)
            postalCodeTimeZoneMapper.callApi(object, null);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(response.getBody());
  }

  @Test
  void callApiInvalidAction() {
    Object object = testUtil.getPostalCodeTimezoneUpload("DEL");

    Exception exception =
        Assertions.assertThrows(
            CsvDataValidationException.class, () -> postalCodeTimeZoneMapper.callApi(object, null));

    Assertions.assertNotNull(exception);
  }
}
