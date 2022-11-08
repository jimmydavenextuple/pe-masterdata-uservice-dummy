package com.nextuple.csvdownload.service;

import static org.mockito.Mockito.*;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimeZoneServiceTest {

  @Mock private PostalCodeTimezoneFeign postalCodeTimezoneFeign;

  @InjectMocks private PostalCodeTimeZoneService postalCodeTimeZoneService;

  @InjectMocks TestUtil testUtil;

  @Test
  void getFsaList() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(List.of(TestUtil.DESTINATION_FSA)).build());

    List<String> fsaList =
        postalCodeTimeZoneService.getFSAsByOrgIdAndState(TestUtil.ORG_ID, TestUtil.SOURCE_REGION);
    Assertions.assertFalse(CollectionUtils.isEmpty(fsaList));
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListEmptyList() {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneService.getFSAsByOrgIdAndState(
                    TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListNullResponse() {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneService.getFSAsByOrgIdAndState(
                    TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountry() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(
            BaseResponse.builder().payload(List.of(testUtil.getPostalCodeTimezoneDto())).build());

    List<PostalCodeTimezoneDto> postalCodeTimezoneDtoList =
        postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(TestUtil.ORG_ID, "CANADA");
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodeTimezoneDtoList));
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountryEmptyList() {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(
                    TestUtil.ORG_ID, "CANADA"));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimeZoneByOrgIdAndCountryNullResponse() {
    when(postalCodeTimezoneFeign.getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () ->
                postalCodeTimeZoneService.getPostalCodeTimeZoneByOrgIdAndCountry(
                    TestUtil.ORG_ID, "CANADA"));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodeTimeZoneForOrgIdAndCountry(anyString(), anyString());
  }
}
