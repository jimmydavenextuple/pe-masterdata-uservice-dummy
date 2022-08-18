package com.hbc.jobs.consumers.util;

import java.util.Optional;

public class UriBuilder {

  private UriBuilder() {}

  /**
   * @param jobType
   * @param days
   * @param sortBy
   * @param sortOrder
   * @param currentPageNo
   * @param pageSize
   * @param totalPages
   * @param uriType
   * @return
   */
  @SuppressWarnings("squid:S107")
  public static String buildUriForPagination(
      Optional<String> jobType,
      Optional<Integer> days,
      String sortBy,
      String sortOrder,
      int currentPageNo,
      int pageSize,
      int totalPages,
      String uriType) {
    if (uriType.equalsIgnoreCase("next")) {
      if (currentPageNo >= totalPages) {
        return "";
      }
      return String.format(
          "/jobs?jobType=%s&days=%s&sortBy=%s&sortOrder=%s&pageNo=%s&pageSize=%s",
          jobType.orElse(""), days.orElse(0), sortBy, sortOrder, (currentPageNo + 1), pageSize);
    } else {
      if (currentPageNo == 1) {
        return "";
      }
      return String.format(
          "/jobs?jobType=%s&days=%s&sortBy=%s&sortOrder=%s&pageNo=%s&pageSize=%s",
          jobType.orElse(""), days.orElse(0), sortBy, sortOrder, (currentPageNo - 1), pageSize);
    }
  }
}
