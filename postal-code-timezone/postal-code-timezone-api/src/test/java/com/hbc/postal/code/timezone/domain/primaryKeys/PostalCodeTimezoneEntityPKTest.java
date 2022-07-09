package com.hbc.postal.code.timezone.domain.primaryKeys;

import static org.junit.jupiter.api.Assertions.*;

import com.hbc.postal.code.timezone.utils.PostalCodeTimezoneConstants;
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
        new PostalCodeTimezoneEntityPK(
            PostalCodeTimezoneConstants.ORG_ID, PostalCodeTimezoneConstants.POSTAL_CODE_PREFIX);
    assertNotNull(postalCodeTimezoneEntityPK.getOrgId());
    assertNotNull(postalCodeTimezoneEntityPK.getPostalCodePrefix());
  }
}
