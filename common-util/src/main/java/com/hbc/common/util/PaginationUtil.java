package com.hbc.common.util;

public class PaginationUtil {

  private PaginationUtil() {
    // Use static methods
  }

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
