package com.nextuple.pe.masterdata.service.exception;

import com.nextuple.pe.masterdata.error.ErrorResponse;
import com.nextuple.pe.masterdata.exception.ItemDomainException;
import com.nextuple.pe.masterdata.exception.MasterDataExceptionHandler;
import com.nextuple.pe.masterdata.exception.NodeDomainException;
import com.nextuple.pe.masterdata.service.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class MasterDataExceptionHandlerTest {

  @InjectMocks TestUtil testUtil;

  @InjectMocks MasterDataExceptionHandler masterDataExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling node domain exception")
  void handleNodeDomainException() {
    NodeDomainException exception =
        new NodeDomainException("Internal Server Error", TestUtil.NODE_ID, TestUtil.ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        masterDataExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }

  @Test
  @DisplayName("Test for handling item domain exception")
  void handleItemDomainException() {
    ItemDomainException exception =
        new ItemDomainException(
            "Internal Server Error", TestUtil.ITEM_ID, TestUtil.ORG_ID, TestUtil.UOM);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        masterDataExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
