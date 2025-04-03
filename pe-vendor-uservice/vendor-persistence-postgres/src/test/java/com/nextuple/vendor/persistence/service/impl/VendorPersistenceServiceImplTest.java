package com.nextuple.vendor.persistence.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
}
