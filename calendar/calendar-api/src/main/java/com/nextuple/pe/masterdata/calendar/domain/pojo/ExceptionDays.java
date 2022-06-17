package com.nextuple.pe.masterdata.calendar.domain.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class ExceptionDays implements Serializable {

  private String date;
  private String reason;
}
