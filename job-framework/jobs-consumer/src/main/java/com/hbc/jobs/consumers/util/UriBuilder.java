package com.hbc.jobs.consumers.util;

import java.util.Optional;

public class UriBuilder {

  private UriBuilder() {}

  /**
   * @param jobType
   * @param days
   * @param sortField
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
      String sortField,
      String sortOrder,
      int currentPageNo,
      int pageSize,
      int totalPages,
      String uriType) {
    if (uriType.equalsIgnoreCase("next")) {
      if (currentPageNo >= totalPages) {
        return null;
      }
      return "/jobs?jobType="
          + jobType.orElse("")
          + "&days="
          + days.orElse(0)
          + "&sortField="
          + sortField
          + "&sortOrder="
          + sortOrder
          + "&pageNo="
          + (currentPageNo + 1)
          + "&pageSize="
          + pageSize;
    } else {
      if (currentPageNo == 1) {
        return null;
      }
      return "/jobs?jobType="
          + jobType.orElse("")
          + "&days="
          + days.orElse(0)
          + "&sortField="
          + sortField
          + "&sortOrder="
          + sortOrder
          + "&pageNo="
          + (currentPageNo - 1)
          + "&pageSize="
          + pageSize;
    }
  }
}
