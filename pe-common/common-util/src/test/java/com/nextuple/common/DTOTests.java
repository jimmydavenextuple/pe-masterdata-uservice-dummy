package com.nextuple.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

class DTOTests {

  @Test
  void testAllPojos() {

    assertNotNull(
        ValidatorBuilder.create().with(new GetterTester()).with(new SetterTester()).build());
  }
}
