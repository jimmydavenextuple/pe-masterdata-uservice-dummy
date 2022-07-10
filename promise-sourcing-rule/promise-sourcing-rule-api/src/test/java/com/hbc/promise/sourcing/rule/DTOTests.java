package com.hbc.promise.sourcing.rule;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import java.util.List;

class DTOTests {

  private List<PojoClass> pojoClassList =
      PojoClassFactory.getPojoClassesRecursively("com.hbc", null);

  @Test
  void testAllPojos() {

    ValidatorBuilder.create()
        .with(new GetterTester())
        .with(new SetterTester())
        .build()
        .validate(pojoClassList);
  }
}
