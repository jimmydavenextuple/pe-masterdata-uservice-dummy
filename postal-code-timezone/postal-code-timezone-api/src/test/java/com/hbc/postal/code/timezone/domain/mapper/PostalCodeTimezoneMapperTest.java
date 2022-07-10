package com.hbc.postal.code.timezone.domain.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hbc.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.hbc.postal.code.timezone.api.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.api.domain.inbound.UpdatePostalCodeTimezoneRequest;
import com.hbc.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.hbc.postal.code.timezone.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class PostalCodeTimezoneMapperTest {

  @InjectMocks private TestUtil testUtil;

  private static final PostalCodeTimezoneMapper INSTANCE =
      Mappers.getMapper(PostalCodeTimezoneMapper.class);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void convertToPostalCodeTimezoneEntityTest() {
    PostalCodeTimezoneDto postalCodeTimezoneDto = testUtil.getPostalCodeTimezoneDto();
    PostalCodeTimezoneEntity postalCodeTimezoneEntity =
        INSTANCE.convertToPostalCodeTimezoneEntity(postalCodeTimezoneDto);
    assertEquals(postalCodeTimezoneDto.getOrgId(), postalCodeTimezoneEntity.getOrgId());
  }

  @Test
  void convertToPostalCodeTimezoneDtoTest() {
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();
    PostalCodeTimezoneDto postalCodeTimezoneDto =
        INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntity);
    assertEquals(postalCodeTimezoneEntity.getOrgId(), postalCodeTimezoneDto.getOrgId());
  }

  @Test
  void convertFromCreatePostalCodeTimezoneRequestToEntityTest() {
    CreatePostalCodeTimezoneRequest createPostalCodeTimezoneRequest =
        testUtil.getCreatePostalCodeTimezoneRequest();
    PostalCodeTimezoneEntity postalCodeTimezoneEntity =
        INSTANCE.convertFromCreatePostalCodeTimezoneRequestToEntity(
            createPostalCodeTimezoneRequest);
    assertEquals(createPostalCodeTimezoneRequest.getOrgId(), postalCodeTimezoneEntity.getOrgId());
  }

  @Test
  void insertValuesFromUpdatePostalCodeTimezoneRequestToEntityTest() {
    UpdatePostalCodeTimezoneRequest updatePostalCodeTimezoneRequest =
        testUtil.getUpdatePostalCodeTimezoneRequest();
    PostalCodeTimezoneEntity postalCodeTimezoneEntity = testUtil.getPostalCodeTimezoneEntity();

    INSTANCE.insertValuesFromUpdatePostalCodeTimezoneRequestToEntity(
        updatePostalCodeTimezoneRequest, postalCodeTimezoneEntity);
    assertEquals(updatePostalCodeTimezoneRequest.getCity(), postalCodeTimezoneEntity.getCity());
  }
}
