package com.nextuple.vendor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.vendor.TestUtil;
import com.nextuple.vendor.domain.inbound.VendorRequest;
import com.nextuple.vendor.domain.outbound.VendorResponse;
import com.nextuple.vendor.persistence.domain.VendorDomainDto;
import com.nextuple.vendor.persistence.exception.VendorDomainException;
import com.nextuple.vendor.persistence.service.VendorPersistenceService;
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
}
