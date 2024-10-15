/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.postal.code.timezone.persistence.service.impl;

import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.ID;
import static com.nextuple.postal.code.timezone.persistence.service.impl.TestUtil.ORG_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.postal.code.timezone.persistence.domain.CustomRegionDomainDto;
import com.nextuple.postal.code.timezone.persistence.entity.CustomRegionEntity;
import com.nextuple.postal.code.timezone.persistence.mapper.CustomRegionEntityMapper;
import com.nextuple.postal.code.timezone.persistence.repository.CustomRegionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

class CustomRegionPersistenceServiceImplTest {
  @InjectMocks private CustomRegionPersistenceServiceImpl customRegionPersistenceService;

  @Mock private CustomRegionRepository customRegionRepository;

  @Mock private CustomRegionEntityMapper customRegionEntityMapper;

  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    // Added this
    ReflectionTestUtils.setField(
        customRegionPersistenceService, "repository", customRegionRepository);
    ReflectionTestUtils.setField(
        customRegionPersistenceService, "mapper", customRegionEntityMapper);
  }

  @Test
  @DisplayName("Save Custom Region: Happy Path")
  void saveCustomRegionTest() throws PromiseEngineException {
    CustomRegionEntity customRegionEntity = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto customRegionDomainDto = testUtil.getCustomRegionDomainDto();
    when(customRegionEntityMapper.toEntity(customRegionDomainDto)).thenReturn(customRegionEntity);
    when(customRegionEntityMapper.toDomain(customRegionEntity)).thenReturn(customRegionDomainDto);
    when(customRegionRepository.save(any(CustomRegionEntity.class))).thenReturn(customRegionEntity);
    CustomRegionDomainDto receivedCustomRegionResponse =
        customRegionPersistenceService.saveCustomRegion(customRegionDomainDto);
    assertEquals(customRegionDomainDto, receivedCustomRegionResponse);
    verify(customRegionRepository, times(1)).save(any(CustomRegionEntity.class));
  }

  @Test
  @DisplayName("Save Custom Region: Exception")
  void saveCustomRegionExceptionTest() {
    CustomRegionEntity customRegionEntity = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto customRegionDomainDto = testUtil.getCustomRegionDomainDto();
    when(customRegionEntityMapper.toEntity(customRegionDomainDto)).thenReturn(customRegionEntity);
    when(customRegionRepository.save(any(CustomRegionEntity.class)))
        .thenThrow(RuntimeException.class);

    assertThrows(
        RuntimeException.class,
        () -> {
          customRegionPersistenceService.saveCustomRegion(customRegionDomainDto);
        });
    verify(customRegionRepository, times(1)).save(any(CustomRegionEntity.class));
  }

  @Test
  @DisplayName("Get Custom Region: Happy Path")
  void getCustomRegionTimezoneTest() throws PromiseEngineException {
    CustomRegionEntity customRegionEntity = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto customRegionDomainDto = testUtil.getCustomRegionDomainDto();
    when(customRegionEntityMapper.toDomain(customRegionEntity)).thenReturn(customRegionDomainDto);
    when(customRegionRepository.findById(any())).thenReturn(Optional.of(customRegionEntity));

    Optional<CustomRegionDomainDto> received_entityResponse =
        customRegionPersistenceService.fetchRegionByOrgIdAndId(ORG_ID, ID);
    assertEquals(customRegionDomainDto, received_entityResponse.get());
    verify(customRegionRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("Get Custom Region: Exception")
  void getCustomRegionExceptionTest() {
    when(customRegionRepository.findById(any())).thenThrow(RuntimeException.class);

    assertThrows(
        RuntimeException.class,
        () -> {
          customRegionPersistenceService.fetchRegionByOrgIdAndId(ORG_ID, ID);
        });
    verify(customRegionRepository, times(1)).findById(any());
  }

  @Test
  @DisplayName("Delete Custom Region: Happy Path")
  void deleteCustomRegionTest() throws PromiseEngineException {
    CustomRegionEntity customRegionEntity = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto customRegionDomainDto = testUtil.getCustomRegionDomainDto();
    when(customRegionEntityMapper.toEntity(customRegionDomainDto)).thenReturn(customRegionEntity);
    doNothing().when(customRegionRepository).delete(any(CustomRegionEntity.class));
    customRegionPersistenceService.deleteCustomRegion(customRegionDomainDto);
    verify(customRegionRepository, times(1)).delete(any(CustomRegionEntity.class));
  }

  @Test
  @DisplayName("Delete Custom Region: Exception")
  void deleteCustomRegionExceptionTest() {
    CustomRegionEntity customRegionEntity = testUtil.getCustomRegionEntity();
    CustomRegionDomainDto customRegionDomainDto = testUtil.getCustomRegionDomainDto();
    when(customRegionEntityMapper.toEntity(customRegionDomainDto)).thenReturn(customRegionEntity);
    doThrow(new RuntimeException("error"))
        .when(customRegionRepository)
        .delete(any(CustomRegionEntity.class));
    assertThrows(
        RuntimeException.class,
        () -> {
          customRegionPersistenceService.deleteCustomRegion(customRegionDomainDto);
        });
    verify(customRegionRepository, times(1)).delete(any(CustomRegionEntity.class));
  }

  @Test
  @DisplayName("Get Custom Region By orgId and sort order: Happy Path")
  void getCustomRegionByOrgIdDefaultSortOrderTest() throws PromiseEngineException {
    List<CustomRegionEntity> customRegionEntityList = testUtil.getCustomRegionEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<CustomRegionEntity> customRegionEntityPage =
        new PageImpl<>(customRegionEntityList, pageable, customRegionEntityList.size());
    when(customRegionRepository.findCustomRegionByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(customRegionEntityPage);
    Page<CustomRegionDomainDto> response =
        customRegionPersistenceService.getCustomRegionListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC");
    Assertions.assertEquals(customRegionEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("id: ASC", response.getSort().toString());

    verify(customRegionRepository, times(1))
        .findCustomRegionByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get Custom Region By orgId and sort order: Exception")
  void getCustomRegionByOrgIdDESCSortOrderTest() throws PromiseEngineException {
    List<CustomRegionEntity> nodeEntityList = testUtil.getCustomRegionEntityList();

    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<CustomRegionEntity> customRegionEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());

    when(customRegionRepository.findCustomRegionByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(customRegionEntityPage);

    Page<CustomRegionDomainDto> response =
        customRegionPersistenceService.getCustomRegionListByOrgId(
            TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "desc");

    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("id: DESC", response.getSort().toString());

    verify(customRegionRepository, times(1))
        .findCustomRegionByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("Get Custom Region By orgId and sort order: Exception")
  void getCustomRegionByOrgIdTestException() {
    when(customRegionRepository.findCustomRegionByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching custom region list"));

    Exception exception =
        assertThrows(
            PromiseEngineException.class,
            () ->
                customRegionPersistenceService.getCustomRegionListByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while fetching custom region list", exception.getMessage());
    verify(customRegionRepository, times(1))
        .findCustomRegionByOrgId(anyString(), any(Pageable.class));
  }
}
