/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.jobs.consumers.util;

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
      return "/jobs?jobType=%s&days=%s&sortBy=%s&sortOrder=%s&pageNo=%s&pageSize=%s"
          .formatted(
              jobType.orElse(""), days.orElse(0), sortBy, sortOrder, (currentPageNo + 1), pageSize);
    } else {
      if (currentPageNo == 1) {
        return "";
      }
      return "/jobs?jobType=%s&days=%s&sortBy=%s&sortOrder=%s&pageNo=%s&pageSize=%s"
          .formatted(
              jobType.orElse(""), days.orElse(0), sortBy, sortOrder, (currentPageNo - 1), pageSize);
    }
  }
}
