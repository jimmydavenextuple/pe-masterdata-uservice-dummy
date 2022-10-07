package com.hbc.postal.code.timezone.domain.primaryKeys;

import static com.hbc.postal.code.timezone.TestUtil.ORG_ID;
import static com.hbc.postal.code.timezone.TestUtil.POSTAL_CODE_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

import com.hbc.postal.code.timezone.domain.primarykeys.PostalCodeTimezoneEntityPK;
import org.junit.jupiter.api.Test;

class PostalCodeTimezoneEntityPKTest {

  @Test
  void postalCodeTimezoneEntityPKNullTest() {
    PostalCodeTimezoneEntityPK postalCodeTimezoneEntityPK = new PostalCodeTimezoneEntityPK();
    assertNull(postalCodeTimezoneEntityPK.getOrgId());
    assertNull(postalCodeTimezoneEntityPK.getPostalCodePrefix());
  }

  @Test
  void postalCodeTimezoneEntityPKTest() {
    PostalCodeTimezoneEntityPK postalCodeTimezoneEntityPK =
        new PostalCodeTimezoneEntityPK(ORG_ID, POSTAL_CODE_PREFIX);
    assertNotNull(postalCodeTimezoneEntityPK.getOrgId());
    assertNotNull(postalCodeTimezoneEntityPK.getPostalCodePrefix());
  }
}
