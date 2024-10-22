package com.nextuple.common.userexit;

import java.util.Map;

public interface IClassBasedExit<T, G> {
  G fetchResponse(T inputData, Map<String, Object> customAttributeMap);
}
