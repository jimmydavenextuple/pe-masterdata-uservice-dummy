package com.nextuple.postal.code.timezone.domain;

import com.nextuple.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.repository.PostalCodeTimezoneRepository;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import com.nextuple.postal.code.timezone.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.ORG_ID;
import static com.nextuple.postal.code.timezone.utils.PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
