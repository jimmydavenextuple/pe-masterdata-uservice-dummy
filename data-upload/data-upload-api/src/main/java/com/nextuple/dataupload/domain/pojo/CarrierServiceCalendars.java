package com.nextuple.dataupload.domain.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public class CarrierServiceCalendars implements Serializable {
  private static final long serialVersionUID = 5612445993462987234L;

  private String effectiveDate;
  private String calendarId;
}
