package com.nextuple.calendar.domain.pojo;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExceptionDays implements Serializable {
  @NotBlank(message = "Date can't be blank")
  private String date;

  @NotBlank(message = "Reason can't be blank")
  private String reason;
}
