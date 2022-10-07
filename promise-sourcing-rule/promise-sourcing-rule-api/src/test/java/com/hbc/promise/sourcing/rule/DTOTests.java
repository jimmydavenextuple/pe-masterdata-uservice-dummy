package com.hbc.promise.sourcing.rule;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class DTOTests {

  private List<PojoClass> pojoClassList =
      PojoClassFactory.getPojoClassesRecursively("com.hbc", null);

  @Test
  void testAllPojos() {

    ValidatorBuilder.create()
        .with(new GetterTester())
        .with(new SetterTester())
        .build()
            .validate(pojoClassList.stream()
                    .filter(x -> !x.getName().startsWith("com.hbc.postgres.config")).collect(Collectors.toList()));
  }
}
