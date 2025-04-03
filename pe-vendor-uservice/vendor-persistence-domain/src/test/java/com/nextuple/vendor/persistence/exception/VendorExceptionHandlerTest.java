package com.nextuple.vendor.persistence.exception;

import com.nextuple.common.response.error.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class VendorExceptionHandlerTest {

  public static final String VENDOR_ID = "vendor-1";
  public static final String ORG_ID = "org-1";

  @InjectMocks VendorExceptionHandler vendorExceptionHandler;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Test for handling vendor domain exception")
  void handleVendorDomainException() {
    VendorDomainException exception =
        new VendorDomainException("Internal Server Error", VENDOR_ID, ORG_ID);

    ResponseEntity<ErrorResponse> errorResponseResponseEntity =
        vendorExceptionHandler.handleOtherException(exception);

    Assertions.assertEquals(
        "Internal Server Error", errorResponseResponseEntity.getBody().getMessage());
  }
}
