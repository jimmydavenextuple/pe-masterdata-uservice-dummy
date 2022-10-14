package com.hbc.postal.code.timezone.domain;

import static com.hbc.postal.code.timezone.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.PromiseEngineException;
import com.hbc.postal.code.timezone.TestUtil;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.domain.repository.PostalCodeTimezoneRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

class PostalCodeTimezoneDomainTest {
  @InjectMocks private PostalCodeTimezoneDomain postalCodeTimezoneDomain;

  @Mock private PostalCodeTimezoneRepository postalCodeTimezoneRepository;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void savePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneRepository.save(any(PostalCodeTimezoneEntity.class)))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneEntity received_postalCodeTimezoneEntity =
        postalCodeTimezoneDomain.savePostalCodeTimezone(postalCodeTimezoneEntity);
    assertEquals(postalCodeTimezoneEntity, received_postalCodeTimezoneEntity);
  }

  @Test
  void savePostalCodeTimezoneExceptionTest() {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneRepository.save(any(PostalCodeTimezoneEntity.class)))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneDomain.savePostalCodeTimezone(postalCodeTimezoneEntity);
        });
  }

  @Test
  void getPostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneRepository.findByOrgIdAndPostalCodePrefix(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneEntity received_entity =
        postalCodeTimezoneDomain.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
    assertEquals(ORG_ID, received_entity.getOrgId());
  }

  @Test
  void getPostalCodeTimezoneExceptionTest() {
    when(postalCodeTimezoneRepository.findByOrgIdAndPostalCodePrefix(anyString(), anyString()))
        .thenThrow(NullPointerException.class);

    assertThrows(
        PromiseEngineException.class,
        () -> {
          postalCodeTimezoneDomain.getPostalCodeTimezone(ORG_ID, POSTAL_CODE_PREFIX);
        });
  }

  @Test
  void deletePostalCodeTimezoneTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    when(postalCodeTimezoneRepository.findByOrgIdAndPostalCodePrefix(anyString(), anyString()))
        .thenReturn(postalCodeTimezoneEntity);

    PostalCodeTimezoneEntity received_entity =
        postalCodeTimezoneDomain.deletePostalCodeTimezone(postalCodeTimezoneEntity);
    assertEquals(ORG_ID, received_entity.getOrgId());
    verify(postalCodeTimezoneRepository, times(1)).delete(postalCodeTimezoneEntity);
  }

  @Test
  void getPostalCodeTimezoneForOrgIdTest() throws PromiseEngineException {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    when(postalCodeTimezoneRepository.findByOrgId(anyString()))
        .thenReturn(List.of(postalCodeTimezoneEntity));

    List<PostalCodeTimezoneEntity> postalCodeTimezoneEntities =
        postalCodeTimezoneDomain.getPostalCodeTimezoneForOrgId(ORG_ID);

    assertEquals(1, postalCodeTimezoneEntities.size());
    assertEquals(postalCodeTimezoneEntity.getState(), postalCodeTimezoneEntities.get(0).getState());
    verify(postalCodeTimezoneRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void getPostalCodeTimezoneForOrgIdExceptionTest() {
    when(postalCodeTimezoneRepository.findByOrgId(anyString()))
        .thenThrow(NullPointerException.class);

    Exception ex =
        assertThrows(
            PromiseEngineException.class,
            () -> {
              postalCodeTimezoneDomain.getPostalCodeTimezoneForOrgId(ORG_ID);
            });

    assertEquals("Postal Code Timezone not found for a given orgId.", ex.getMessage());
    verify(postalCodeTimezoneRepository, times(1)).findByOrgId(anyString());
  }

  @Test
  void getPostalCodePrefixForOrgIdAndState() throws PromiseEngineException {
    when(postalCodeTimezoneRepository.findPostalCodePrefixListByOrgIdAndState(
            anyString(), anyString()))
        .thenReturn(List.of(POSTAL_CODE_PREFIX, POSTAL_CODE_PREFIX_2));

    List<String> postalCodePrefixList =
        postalCodeTimezoneDomain.getPostalCodePrefixForOrgIdAndState(ORG_ID, STATE);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodePrefixList));
    verify(postalCodeTimezoneRepository, times(1))
        .findPostalCodePrefixListByOrgIdAndState(anyString(), anyString());
  }

  @Test
  void getPostalCodePrefixForOrgIdAndStateException() throws PromiseEngineException {
    when(postalCodeTimezoneRepository.findPostalCodePrefixListByOrgIdAndState(
            anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching postal code prefix list"));

    Exception exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> postalCodeTimezoneDomain.getPostalCodePrefixForOrgIdAndState(ORG_ID, STATE));
    Assertions.assertNotNull(exception);
  }

  @Test
  void getPostCodeTimeZoneByOrgIdAndCountry() throws PromiseEngineException {
    when(postalCodeTimezoneRepository.findByOrgIdAndCountry(anyString(), anyString()))
        .thenReturn(List.of(testUtil.getPostalCodeTimezoneEntity()));

    List<PostalCodeTimezoneEntity> postalCodePrefixList =
        postalCodeTimezoneDomain.getPostCodeTimeZoneByOrgIdAndCountry(ORG_ID, COUNTRY);
    Assertions.assertFalse(CollectionUtils.isEmpty(postalCodePrefixList));
    verify(postalCodeTimezoneRepository, times(1)).findByOrgIdAndCountry(anyString(), anyString());
  }

  @Test
  void getPostCodeTimeZoneByOrgIdAndCountryException() {
    when(postalCodeTimezoneRepository.findByOrgIdAndCountry(anyString(), anyString()))
        .thenThrow(new RuntimeException("Error while fetching postal code prefix list"));

    Exception exception =
        Assertions.assertThrows(
            PromiseEngineException.class,
            () -> postalCodeTimezoneDomain.getPostCodeTimeZoneByOrgIdAndCountry(ORG_ID, COUNTRY));
    Assertions.assertNotNull(exception);
  }

  @Test
  void getRecordsForOrgId() {
    when(postalCodeTimezoneRepository.findRecordsByOrgId(anyString()))
        .thenReturn(testUtil.getMarketRegion());

    Assertions.assertDoesNotThrow(() -> postalCodeTimezoneRepository.findRecordsByOrgId(ORG_ID));
  }

  @Test
  void getRecordsForOrgIdException() {
    when(postalCodeTimezoneRepository.findRecordsByOrgId(anyString()))
        .thenThrow(new RuntimeException());

    Assertions.assertThrows(
        RuntimeException.class, () -> postalCodeTimezoneRepository.findRecordsByOrgId(ORG_ID));
  }
}
