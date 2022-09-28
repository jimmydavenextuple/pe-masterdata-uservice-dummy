package com.hbc.common.util;

import com.hbc.common.exception.CommonServiceException;
import java.util.Date;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class DateValidationUtilTest {

  @InjectMocks private DateValidationUtil dateValidation;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void validateBufferStartAndEndDateTest() {
    Date bufferStartDate = LocalDate.now().toDate();
    Date bufferEndDate = LocalDate.now().minusDays(3).toDate();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> dateValidation.validateBufferStartAndEndDate(bufferStartDate, bufferEndDate));
    Assertions.assertEquals(
        "bufferEndDate should be greater than or equal to bufferStartDate", exception.getMessage());
  }

  @Test
  void validateBufferStartAndEndDateTestForNullEndDate() {
    Date bufferStartDate = LocalDate.now().toDate();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> dateValidation.validateBufferStartAndEndDate(bufferStartDate, null));
    Assertions.assertEquals(
        "Either both bufferStartDate and bufferEndDate should be null else both should have some value defined",
        exception.getMessage());
  }

  @Test
  void validateBufferStartAndEndDateTestForNullStartDate() {
    Date bufferEndDate = LocalDate.now().toDate();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> dateValidation.validateBufferStartAndEndDate(null, bufferEndDate));
    Assertions.assertEquals(
        "Either both bufferStartDate and bufferEndDate should be null else both should have some value defined",
        exception.getMessage());
  }

  @Test
  void validateBufferStartAndEndDateTestForNonNullStartAndEndDate() {
    Date bufferStartDate = LocalDate.now().toDate();
    Date bufferEndDate = LocalDate.now().plusDays(3).toDate();
    Assertions.assertDoesNotThrow(
        () -> dateValidation.validateBufferStartAndEndDate(bufferStartDate, bufferEndDate));
  }

  @Test
  void validateBufferStartAndEndDateTestForNullStartAndEndDate() {
    Assertions.assertDoesNotThrow(() -> dateValidation.validateBufferStartAndEndDate(null, null));
  }
}
