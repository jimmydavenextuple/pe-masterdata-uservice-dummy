package com.hbc.item.exception;

import com.hbc.common.response.error.ErrorResponse;
import com.hbc.item.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class ItemExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks ItemExceptionHandler itemExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling item domain exception")
  void handleItemDomainException() {
    ItemDomainException exception =
        new ItemDomainException(
            "Internal Server Error", TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        itemExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling item batching domain exception")
  void handleItemBatchingDomainException() {
    ItemBatchingDomainException exception =
        new ItemBatchingDomainException(
            "Internal Server Error", List.of(TestUtil.ITEM_ID), TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        itemExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
