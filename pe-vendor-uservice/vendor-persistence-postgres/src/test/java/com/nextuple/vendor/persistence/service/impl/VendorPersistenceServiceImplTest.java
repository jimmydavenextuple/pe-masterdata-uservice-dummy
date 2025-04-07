/*
 * Copyright (c) 2025., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.vendor.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.entity.VendorEntity;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.mapper.VendorEntityMapper;
import com.nextuple.vendor.persistence.repository.VendorRepository;
import com.nextuple.vendor.persistence.util.TestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

class VendorPersistenceServiceImplTest {
  @Mock private VendorRepository vendorRepository;
  @Mock private VendorEntityMapper vendorEntityMapper;
  @InjectMocks private VendorPersistenceServiceImpl vendorPersistenceService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(vendorPersistenceService, "repository", vendorRepository);
    ReflectionTestUtils.setField(vendorPersistenceService, "mapper", vendorEntityMapper);
  }

  @Test
  void saveVendorTest() throws VendorDomainException {
    VendorEntity vendorEntity = testUtil.getVendorEntity();
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class))).thenReturn(vendorEntity);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    when(vendorRepository.save(any())).thenReturn(vendorEntity);
    VendorDomainDto vendor =
        vendorPersistenceService.saveVendorDetails(testUtil.getVendorDomainDto());
    Assertions.assertEquals(vendorEntity.getVendorId(), vendor.getVendorId());
    verify(vendorRepository, times(1)).save(any());
  }

  @Test
  void saveVendorExceptionTest() {
    when(vendorRepository.save(any())).thenThrow(new RuntimeException("Error while saving"));
    Exception exception =
        assertThrows(
            VendorDomainException.class,
            () -> vendorPersistenceService.saveVendorDetails(testUtil.getVendorDomainDto()));
    Assertions.assertEquals("Error while saving the vendor", exception.getMessage());
    verify(vendorRepository, times(1)).save(any());
  }

  @Test
  void getVendorDetailsTest() throws VendorDomainException {
    VendorEntity vendorEntity = testUtil.getVendorEntity();
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class))).thenReturn(vendorEntity);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    when(vendorRepository.findById(any())).thenReturn(Optional.of(vendorEntity));
    Optional<VendorDomainDto> optionalVendorDomainDto =
        vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    Assertions.assertEquals(
        vendorEntity.getVendorId(), optionalVendorDomainDto.get().getVendorId());
    verify(vendorRepository, times(1)).findById(any());
  }

  @Test
  void getVendorDetailsTestException() {
    when(vendorRepository.findById(any()))
        .thenThrow(new RuntimeException("Error while fetching details"));
    Exception exception =
        assertThrows(
            VendorDomainException.class,
            () ->
                vendorPersistenceService.findVendorByVendorIdAndOrgId(
                    TestUtil.VENDOR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while finding vendor", exception.getMessage());
    verify(vendorRepository, times(1)).findById(any());
  }

  @Test
  void vendorDeletionTest() throws VendorDomainException {
    when(vendorEntityMapper.toEntity(any(VendorDomainDto.class)))
        .thenReturn(testUtil.getVendorEntity());
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDto());
    doNothing().when(vendorRepository).delete(any());
    vendorPersistenceService.deleteVendor(testUtil.getVendorDomainDto());
    verify(vendorRepository, times(1)).delete(any());
  }

  @Test
  void vendorDeletionTestException() {
    doThrow(new RuntimeException("error while deleting")).when(vendorRepository).delete(any());
    Exception exception =
        assertThrows(
            VendorDomainException.class,
            () -> vendorPersistenceService.deleteVendor(testUtil.getVendorDomainDto()));
    Assertions.assertEquals("Error while deleting vendor", exception.getMessage());
    verify(vendorRepository, times(1)).delete(any());
  }

  @Test
  void getVendorByOrgIdDefaultSortOrderTest() throws VendorDomainException {
    List<VendorEntity> nodeEntityList = testUtil.getVendorEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).ascending());
    Page<VendorEntity> vendorEntityPage =
        new PageImpl<>(nodeEntityList, pageable, nodeEntityList.size());
    when(vendorRepository.findVendorByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(vendorEntityPage);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDtoList().get(0))
        .thenReturn(testUtil.getVendorDomainDtoList().get(1));
    Page<VendorDomainDto> response =
        vendorPersistenceService.getVendorByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC");
    Assertions.assertEquals(nodeEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("vendorId: ASC", response.getSort().toString());
    verify(vendorRepository, times(1)).findVendorByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getVendorByOrgIdDESCSortOrderTest() throws VendorDomainException {
    List<VendorEntity> vendorEntityList = testUtil.getVendorEntityList();
    Pageable pageable = PageRequest.of(1, 1, Sort.by(TestUtil.SORT_BY).descending());
    Page<VendorEntity> vendorEntityPage =
        new PageImpl<>(vendorEntityList, pageable, vendorEntityList.size());
    when(vendorRepository.findVendorByOrgId(anyString(), any(Pageable.class)))
        .thenReturn(vendorEntityPage);
    when(vendorEntityMapper.toDomain(any(VendorEntity.class)))
        .thenReturn(testUtil.getVendorDomainDtoList().get(0))
        .thenReturn(testUtil.getVendorDomainDtoList().get(1));
    Page<VendorDomainDto> response =
        vendorPersistenceService.getVendorByOrgId(TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "desc");
    Assertions.assertEquals(vendorEntityList.size(), response.getContent().size());
    Assertions.assertEquals(2, response.getTotalPages());
    Assertions.assertEquals(1, response.getPageable().getPageSize());
    Assertions.assertEquals(2, response.getTotalElements());
    Assertions.assertEquals("vendorId: DESC", response.getSort().toString());
    verify(vendorRepository, times(1)).findVendorByOrgId(anyString(), any(Pageable.class));
  }

  @Test
  void getVendorByOrgIdTestException() {
    when(vendorRepository.findVendorByOrgId(anyString(), any(Pageable.class)))
        .thenThrow(new RuntimeException("Error while fetching vendor list"));
    Exception exception =
        assertThrows(
            VendorDomainException.class,
            () ->
                vendorPersistenceService.getVendorByOrgId(
                    TestUtil.ORG_ID, 1, 1, TestUtil.SORT_BY, "ASC"));
    Assertions.assertEquals("Error while finding vendor list", exception.getMessage());
    verify(vendorRepository, times(1)).findVendorByOrgId(anyString(), any(Pageable.class));
  }
}
