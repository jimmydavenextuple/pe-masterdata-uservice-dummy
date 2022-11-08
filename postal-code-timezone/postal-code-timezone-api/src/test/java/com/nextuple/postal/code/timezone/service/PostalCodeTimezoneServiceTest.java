package com.nextuple.postal.code.timezone.service;

import static com.nextuple.postal.code.timezone.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.TestUtil;
import com.nextuple.postal.code.timezone.api.domain.dto.MarketRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class PostalCodeTimezoneServiceTest {
  @Mock private PostalCodeTimezoneDomain postalCodeTimezoneDomain;
  @InjectMocks private PostalCodeTimezoneService postalCodeTimezoneService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createPostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    when(postalCodeTimezoneDomain.savePostalCodeTimezone(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto received_dto =
        postalCodeTimezoneService.createPostalCodeTimezone(createPostalCodeTimezoneRequest);
    assertEquals(createPostalCodeTimezoneRequest.getOrgId(), received_dto.getOrgId());
    verify(postalCodeTimezoneDomain, times(1))
        .savePostalCodeTimezone(any(PostalCodeTimezoneEntity.class));
  }

  @Test
  void getPostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneDomain.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto postalCodeTimezoneDto =
        postalCodeTimezoneService.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
    assertEquals(postalCodeTimezoneDto.getOrgId(), postalCodeTimezoneEntity.getOrgId());
    verify(postalCodeTimezoneDomain, times(1)).getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void getPostalCodeTimezoneNotFoundTest() throws PromiseEngineException {
    when(postalCodeTimezoneDomain.getPostalCodeTimezone(anyString(), anyString())).thenReturn(null);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneService.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
        });
    verify(postalCodeTimezoneDomain, times(1)).getPostalCodeTimezone(anyString(), anyString());
  }

  @Test
  void updatePostalCodeTimezoneTest() throws PromiseEngineException, CommonServiceException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    UpdatePostalCodeTimezoneRequest updatePostalCodeTimezoneRequest =
        testUtil.getUpdatePostalCodeTimezoneRequest();

    when(postalCodeTimezoneDomain.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneDomain.savePostalCodeTimezone(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto updated_postalCodeTimezoneDto =
        postalCodeTimezoneService.updatePostalCodeTimezone(
            ORG_ID, POSTAL_CODE_PREFIX, updatePostalCodeTimezoneRequest);
    assertEquals(
        updatePostalCodeTimezoneRequest.getCountry(), updated_postalCodeTimezoneDto.getCountry());
    assertEquals(
        updatePostalCodeTimezoneRequest.getCity(), updated_postalCodeTimezoneDto.getCity());
    verify(postalCodeTimezoneDomain, times(1)).getPostalCodeTimezone(anyString(), anyString());
    verify(postalCodeTimezoneDomain, times(1))
        .savePostalCodeTimezone(any(PostalCodeTimezoneEntity.class));
  }

  @Test
  void deletePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezoneDomain.getPostalCodeTimezone(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);
    when(postalCodeTimezoneDomain.deletePostalCodeTimezone(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneDto deleted_postalCodeTimezoneDto =
        postalCodeTimezoneService.deletePostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
    assertEquals(postalCodeTimezoneEntity.getOrgId(), deleted_postalCodeTimezoneDto.getOrgId());
  }

  @Test
  void fetchPostalCodePrefixListTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezoneDomain.getPostalCodeTimezoneForOrgId(anyString()))
        .thenReturn(List.of(postalCodeTimezoneEntity));

    List<PostalCodePrefixDto> postalCodePrefixDtoList =
        postalCodeTimezoneService.fetchPostalCodePrefixList(ORG_ID);

    assertEquals(1, postalCodePrefixDtoList.size());
    assertEquals(postalCodeTimezoneEntity.getState(), postalCodePrefixDtoList.get(0).getState());
    verify(postalCodeTimezoneDomain, times(1)).getPostalCodeTimezoneForOrgId(anyString());
  }

  @Test
  void fetchPostalCodePrefixForOrgIdAndState() throws PromiseEngineException {
    when(postalCodeTimezoneDomain.getPostalCodePrefixForOrgIdAndState(anyString(), anyString()))
        .thenReturn(List.of(POSTAL_CODE_PREFIX, POSTAL_CODE_PREFIX_2));

    List<String> postalCodeTimeZonePrefixList =
        postalCodeTimezoneService.fetchPostalCodePrefixForOrgIdAndState(ORG_ID, STATE);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodeTimeZonePrefixList));
    verify(postalCodeTimezoneDomain, times(1))
        .getPostalCodePrefixForOrgIdAndState(anyString(), anyString());
  }

  @Test
  void createPostalCodeTimezoneTestException() throws PromiseEngineException {
    CreatePostalCodeTimezoneRequest request1 = testUtil.getCreatePostalCodeTimezoneRequest();
    request1.setCountry("IND");
    CreatePostalCodeTimezoneRequest request2 = testUtil.getCreatePostalCodeTimezoneRequest();
    request2.setTimeZone("IST");

    Assertions.assertThrows(
        CommonServiceException.class,
        () -> postalCodeTimezoneService.createPostalCodeTimezone(request1));
    Assertions.assertThrows(
        CommonServiceException.class,
        () -> postalCodeTimezoneService.createPostalCodeTimezone(request2));
    verify(postalCodeTimezoneDomain, times(0))
        .savePostalCodeTimezone(any(PostalCodeTimezoneEntity.class));
  }

  @Test
  void fetchPostalCodeTimezoneByOrgIdAndCountryTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezoneDomain.getPostCodeTimeZoneByOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(List.of(postalCodeTimezoneEntity));

    List<PostalCodeTimezoneDto> postalCodePrefixDtoList =
        postalCodeTimezoneService.fetchPostalCodeTimezoneByOrgIdAndCountry(ORG_ID, COUNTRY);

    assertEquals(1, postalCodePrefixDtoList.size());
    assertEquals(postalCodeTimezoneEntity.getState(), postalCodePrefixDtoList.get(0).getState());
    verify(postalCodeTimezoneDomain, times(1))
        .getPostCodeTimeZoneByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getMarketRegionForOrgId() throws PromiseEngineException {

    when(postalCodeTimezoneDomain.getRecordsForOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegion());

    List<MarketRegionInfo> marketRegionDtos =
        postalCodeTimezoneService.getMarketRegionForOrgId(ORG_ID);

    assertEquals(1, marketRegionDtos.size());
    assertEquals(1, marketRegionDtos.get(0).getNoOfPostalCodePrefixes());
    assertEquals(4, marketRegionDtos.get(0).getNoOfCities());

    verify(postalCodeTimezoneDomain, times(1)).getRecordsForOrgId(anyString());
  }

  @Test
  void getMarketRegionForOrgIdException() throws PromiseEngineException {

    when(postalCodeTimezoneDomain.getRecordsForOrgId(anyString())).thenReturn(null);

    Assertions.assertThrows(
        PromiseEngineException.class,
        () -> postalCodeTimezoneService.getMarketRegionForOrgId(ORG_ID));

    verify(postalCodeTimezoneDomain, times(1)).getRecordsForOrgId(anyString());
  }
}
