package com.hbc.transit.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.transit.TestUtil;
import org.junit.jupiter.api.Test;

class TransitIdTest {

  @Test
  void transitIdNullTest() {
    TransitId transitId = new TransitId();
    assertNull(transitId.getCarrierServiceId());
    assertNull(transitId.getDestinationGeozone());
    assertNull(transitId.getOrgId());
    assertNull(transitId.getSourceGeozone());
  }

  @Test
  void transitIdTest() {
    TransitId transitId =
        new TransitId(
            TestUtil.ORG_ID,
            TestUtil.SOURCE_GEOZONE,
            TestUtil.DESTINATION_GEOZONE,
            TestUtil.CARRIER_SERVICE_ID);
    assertNotNull(transitId.getCarrierServiceId());
    assertNotNull(transitId.getDestinationGeozone());
    assertNotNull(transitId.getOrgId());
    assertNotNull(transitId.getSourceGeozone());
  }
}
