package com.nextuple.dataupload.common.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class ProcessingTimeBuffer implements Serializable {
  private static final long serialVersionUID = 4050845468045943654L;

  private String serviceOption;
  private Double bufferHours;
  private Date bufferStartDate;
  private Date bufferEndDate;
  private String status;
}
