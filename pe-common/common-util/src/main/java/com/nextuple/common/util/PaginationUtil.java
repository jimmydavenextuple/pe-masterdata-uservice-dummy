package com.nextuple.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtil {

  public static String buildUriForPagination(
      int currentPageNo, int totalPages, String uriType, String uri) {
    if (uriType.equalsIgnoreCase("next")) {
      if (currentPageNo >= totalPages) {
        return "";
      }
    } else {
      if (currentPageNo == 1) {
        return "";
      }
    }
    return uri;
  }
}
