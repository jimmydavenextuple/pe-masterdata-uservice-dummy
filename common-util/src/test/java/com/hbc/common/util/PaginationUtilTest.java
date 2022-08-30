package com.hbc.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PaginationUtilTest {

  @Test
  void buildUriForPaginationPreviousUriTest() {

    /** check the positive use case for previous uri */
    assertEquals(
        "previous_uri", PaginationUtil.buildUriForPagination(2, 5, "previous", "previous_uri"));

    /** check the negative use case for previous uri */
    assertEquals("", PaginationUtil.buildUriForPagination(1, 5, "previous", "previous_uri"));
  }

  @Test
  void buildUriForPaginationNextUriTest() {

    /** check the positive use case for next uri */
    assertEquals("next_uri", PaginationUtil.buildUriForPagination(1, 5, "next", "next_uri"));

    /** check the negative use case for next uri */
    assertEquals("", PaginationUtil.buildUriForPagination(5, 5, "next", "next_uri"));
  }
}
