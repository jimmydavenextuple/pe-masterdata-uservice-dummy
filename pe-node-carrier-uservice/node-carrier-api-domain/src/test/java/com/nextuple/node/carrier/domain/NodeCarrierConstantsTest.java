/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.node.carrier.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextuple.node.carrier.domain.constants.NodeCarrierConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class NodeCarrierConstantsTest {

  @Test
  void nodeCarrierConstantTest()
      throws NoSuchMethodException,
          InvocationTargetException,
          InstantiationException,
          IllegalAccessException {
    Constructor<NodeCarrierConstants> constructor =
        NodeCarrierConstants.class.getDeclaredConstructor();
    assertEquals(false, constructor.isSynthetic());
    constructor.setAccessible(true);
    constructor.newInstance((Object[]) null);
  }
}
