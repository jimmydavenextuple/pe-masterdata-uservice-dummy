package com.nextuple.vendor.persistence.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VendorDomainExceptionTest {

  public static final String VENDOR_ID = "vendor-1";
  public static final String ORG_ID = "org-1";

  @Test
  @DisplayName("Testing VendorDomainException")
  void constructTest() {
    VendorDomainException vendorDomainException =
        new VendorDomainException("test", VENDOR_ID, ORG_ID);
    Assertions.assertEquals("test", vendorDomainException.getMessage());
    Assertions.assertEquals(VENDOR_ID, vendorDomainException.getVendorId());
    Assertions.assertEquals(ORG_ID, vendorDomainException.getOrgId());
  }
}
