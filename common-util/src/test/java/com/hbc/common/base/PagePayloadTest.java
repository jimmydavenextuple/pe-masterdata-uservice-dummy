package com.hbc.common.base;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PagePayloadTest {

  @Test
  void getData() {
    List data = new ArrayList();
    List objects = new ArrayList();
    PagePayload.Pagination pagination = new PagePayload.Pagination();
    pagination.setCurrentPage(1);
    pagination.setNext("nxt");
    pagination.setPrevious("prv");
    pagination.setTotalPages(10);
    pagination.setTotalRecords(20);

    PagePayload payload = new PagePayload();
    PagePayload payload1 = new PagePayload(data, null, null);

    // construct 1 null all
    assertNotNull(payload);
    assertNull(payload.getData());
    assertNull(payload.getPagination());
    assertNull(payload.getAggregation());

    // construct 2 specified args
    assertNotNull(payload1);
    assertEquals(data, payload1.getData());
    assertNull(payload1.getPagination());
    assertNull(payload1.getAggregation());

    // setting and getting test
    payload1.setPagination(pagination);
    payload1.setAggregation(objects);

    assertEquals(pagination, payload1.getPagination());
    assertEquals(objects, payload1.getAggregation());
  }
}
