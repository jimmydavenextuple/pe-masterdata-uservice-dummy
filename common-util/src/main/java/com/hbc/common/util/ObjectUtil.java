package com.hbc.common.util;

import org.springframework.util.StringUtils;

public class ObjectUtil {
  public static boolean isEmptyOrVoidString(String str) {
    return !StringUtils.hasLength(str);
  }

  public static <O> boolean isNull(O obj) {
    return obj == null;
  }
}
