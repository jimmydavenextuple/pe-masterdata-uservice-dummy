package com.nextuple.vendor.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.vendor.persistence.entity.key.VendorKey;
import org.junit.jupiter.api.Test;

public class VendorEntityTest {

  public static final String NODE_ID = "node-1";
  public static final String ORG_ID = "org-1";

  @Test
  void vendorEntityTest() {
    VendorEntity vendorEntity = new VendorEntity();
    assertNull(vendorEntity.getVendorId());
    assertNull(vendorEntity.getVendorDescription());
    assertNull(vendorEntity.getVendorType());
    assertNull(vendorEntity.getOrgId());
  }

  @Test
  void vendorIdNullTest() {
    VendorKey vendorKey = new VendorKey();
    assertNull(vendorKey.getVendorId());
    assertNull(vendorKey.getOrgId());
  }

  @Test
  void vendorIdTest() {
    VendorKey vendorKey = new VendorKey(NODE_ID, ORG_ID);
    assertNotNull(vendorKey.getVendorId());
    assertNotNull(vendorKey.getOrgId());
  }
}
