package com.nextuple.common.userexit;

import java.util.Map;

public class ExampleClassBasedExit implements IClassBasedExit<String, Integer> {
  @Override
  public Integer fetchResponse(String inputData, Map<String, Object> customAttributeMap) {
    return 1;
  }
}
