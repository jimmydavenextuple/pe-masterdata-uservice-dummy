package com.nextuple.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectUtil {
  public static boolean isEmptyOrVoidString(String str) {
    return !StringUtils.hasLength(str);
  }

  public static <O> boolean isNull(O obj) {
    return obj == null;
  }
}
