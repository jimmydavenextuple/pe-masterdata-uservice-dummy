package com.hbc.postal.code.timezone.service;

import static com.hbc.postal.code.timezone.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.postal.code.timezone.TestUtil;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodePrefixDto;
import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
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
  void createPostalCodeTimezoneTest() throws PromiseEngineException {
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
  void updatePostalCodeTimezoneTest() throws PromiseEngineException {
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
}
