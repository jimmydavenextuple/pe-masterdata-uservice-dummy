package com.hbc.csvdownload.service;

import static org.mockito.Mockito.*;

import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.PostalCodeTimezoneServiceException;
import com.hbc.postal.code.timezone.api.domain.feign.PostalCodeTimezoneFeign;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.util.Collections;
import java.util.HashMap;
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

  @Test
  void getFsaList() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(List.of(TestUtil.DESTINATION_FSA)).build());

    List<String> fsaList =
        postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION);
    Assertions.assertFalse(CollectionUtils.isEmpty(fsaList));
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListEmptyList() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(BaseResponse.builder().payload(Collections.emptyList()).build());

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () -> postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListNullResponse() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(null);

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () -> postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getFsaListFeignException() throws PostalCodeTimezoneServiceException {
    when(postalCodeTimezoneFeign.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenThrow(
            new FeignException.BadRequest(
                "Error when fetching fsaList",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Error when fetching fsaList".getBytes()));

    Exception exception =
        Assertions.assertThrows(
            PostalCodeTimezoneServiceException.class,
            () -> postalCodeTimeZoneService.getFsaList(TestUtil.ORG_ID, TestUtil.SOURCE_REGION));
    Assertions.assertNotNull(exception);
    verify(postalCodeTimezoneFeign, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }
}
