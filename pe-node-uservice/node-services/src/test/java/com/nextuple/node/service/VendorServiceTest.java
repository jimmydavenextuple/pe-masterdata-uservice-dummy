package com.nextuple.node.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.node.TestUtil;
import com.nextuple.node.domain.inbound.VendorRequest;
import com.nextuple.node.domain.outbound.VendorResponse;
import com.nextuple.node.persistence.domain.VendorDomainDto;
import com.nextuple.node.persistence.exception.VendorDomainException;
import com.nextuple.node.persistence.service.VendorPersistenceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VendorServiceTest {

  @InjectMocks private VendorService vendorService;

  @InjectMocks private TestUtil testUtil;

  @Mock private VendorPersistenceService vendorPersistenceService;

  @Test
  void createVendorTest() throws VendorDomainException, CommonServiceException {
    VendorDomainDto vendorDomainDto = testUtil.getVendorDomainDto(TestUtil.VENDOR_ID);

    VendorRequest vendorRequest = testUtil.getVendorRequest();
    when(vendorPersistenceService.saveVendorDetails(any(VendorDomainDto.class)))
        .thenReturn(vendorDomainDto);

    VendorResponse received_dto = vendorService.createVendor(testUtil.getVendorRequest());
    Assertions.assertEquals(vendorRequest.getVendorId(), received_dto.getVendorId());
    verify(vendorPersistenceService, times(1)).saveVendorDetails(any(VendorDomainDto.class));
  }
}
