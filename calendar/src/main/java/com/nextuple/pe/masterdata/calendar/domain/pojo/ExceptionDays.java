package com.nextuple.pe.masterdata.calendar.domain.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExceptionDays implements Serializable {

  private String date;
  private String reason;
}
