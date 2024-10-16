/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.promise.sourcing.rule;

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
      PojoClassFactory.getPojoClassesRecursively("com.nextuple", null);

  @Test
  void testAllPojos() {

    ValidatorBuilder.create()
        .with(new GetterTester())
        .with(new SetterTester())
        .build()
        .validate(
            pojoClassList.stream()
                .filter(
                    x ->
                        !x.getName().startsWith("com.nextuple.postgres")
                            && !x.getName().startsWith("com.nextuple.common")
                            && !x.getName().contains("CacheValueWithException"))
                .collect(Collectors.toList()));
  }
}
