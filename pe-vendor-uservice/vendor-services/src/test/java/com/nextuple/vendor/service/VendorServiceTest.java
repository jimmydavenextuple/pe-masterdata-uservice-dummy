package com.nextuple.vendor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.vendor.TestUtil;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.inbound.VendorUpdationRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {
  @InjectMocks private VendorService vendorService;
  @InjectMocks private TestUtil testUtil;
  @Mock private VendorPersistenceService vendorPersistenceService;

  @Test
  void createVendorTest() throws VendorDomainException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    VendorRequest vendorRequest = testUtil.getVendorRequest();
    when(vendorPersistenceService.saveVendorDetails(any(VendorDomainDto.class)))
        .thenReturn(vendorDomainDto);
    VendorResponse receivedDto = vendorService.createVendor(testUtil.getVendorRequest());
    Assertions.assertEquals(vendorRequest.getVendorId(), receivedDto.getVendorId());
    verify(vendorPersistenceService, times(1)).saveVendorDetails(any(VendorDomainDto.class));
  }

  @Test
  void updateVendorDetails_Success() throws VendorDomainException, CommonServiceException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.of(vendorDomainDto));
    when(vendorPersistenceService.saveVendorDetails(any(VendorDomainDto.class)))
        .thenReturn(vendorDomainDto);

    VendorResponse response =
        vendorService.updateVendorDetails(
            TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(TestUtil.VENDOR_ID, response.getVendorId());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
    verify(vendorPersistenceService, times(1)).saveVendorDetails(any(VendorDomainDto.class));
  }

  @Test
  void updateVendorDetails_VendorNotFound() throws VendorDomainException {
    VendorUpdationRequest vendorUpdationRequest = testUtil.getVendorUpdationRequest();
    when(vendorPersistenceService.findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID))
        .thenReturn(Optional.empty());

    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              vendorService.updateVendorDetails(
                  TestUtil.VENDOR_ID, TestUtil.ORG_ID, vendorUpdationRequest);
            });

    Assertions.assertEquals("Vendor not found with given details", exception.getMessage());
    verify(vendorPersistenceService, times(1))
        .findVendorByVendorIdAndOrgId(TestUtil.VENDOR_ID, TestUtil.ORG_ID);
  }
}
