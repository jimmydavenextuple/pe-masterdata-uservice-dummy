package com.nextuple.jobs.consumers.exception;

import static com.nextuple.jobs.consumers.util.Constants.cause;
import static com.nextuple.jobs.consumers.util.Constants.enableSuppression;
import static com.nextuple.jobs.consumers.util.Constants.message;
import static com.nextuple.jobs.consumers.util.Constants.writableStackTrace;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.jobs.framework.common.domain.enums.RecordDataTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FeignClientMapperExceptionTest {
  final RecordDataTypeEnum recordDataType = RecordDataTypeEnum.CSV;

  @Test
  void constructTest() {
    FeignClientMapperException e = new FeignClientMapperException(recordDataType);
    FeignClientMapperException e1 = new FeignClientMapperException(message, recordDataType);
    FeignClientMapperException e2 = new FeignClientMapperException(message, cause, recordDataType);
    FeignClientMapperException e3 = new FeignClientMapperException(cause, recordDataType);
    FeignClientMapperException e4 =
        new FeignClientMapperException(
            message, cause, enableSuppression, writableStackTrace, recordDataType);

    // construct1
    assertNotNull(e);
    Assertions.assertEquals(recordDataType, e.getRecordDataType());
    assertNull(e.getMessage());
    assertNull(e.getCause());

    // construct2
    assertNotNull(e1);
    Assertions.assertEquals(message, e1.getMessage());
    Assertions.assertEquals(recordDataType, e1.getRecordDataType());
    assertNull(e1.getCause());

    // construct3
    assertNotNull(e2);
    Assertions.assertEquals(message, e2.getMessage());
    Assertions.assertEquals(recordDataType, e2.getRecordDataType());
    Assertions.assertEquals(cause, e2.getCause());

    // construct4
    assertNotNull(e3);
    Assertions.assertEquals(recordDataType, e3.getRecordDataType());
    Assertions.assertEquals(cause, e3.getCause());

    // construct5
    assertNotNull(e4);
    Assertions.assertEquals(message, e4.getMessage());
    Assertions.assertEquals(recordDataType, e4.getRecordDataType());
    Assertions.assertEquals(cause, e4.getCause());
  }
}
