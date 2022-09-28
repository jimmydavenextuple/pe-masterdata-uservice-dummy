package com.hbc.common.util;

import com.hbc.common.exception.CommonServiceException;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateValidationUtil {

  public void validateBufferStartAndEndDate(Date startDate, Date endDate)
      throws CommonServiceException {
    if ((Objects.isNull(startDate) && Objects.nonNull(endDate))
        || (Objects.isNull(endDate) && Objects.nonNull(startDate))) {
      throw new CommonServiceException(
          "Either both bufferStartDate and bufferEndDate should be null else both should have some value defined",
          HttpStatus.BAD_REQUEST,
          0x1775,
          null);
    }

    if (Objects.nonNull(endDate) && startDate.after(endDate)) {
      throw new CommonServiceException(
          "bufferEndDate should be greater than or equal to bufferStartDate",
          HttpStatus.BAD_REQUEST,
          0x1775,
          null);
    }
  }
}
