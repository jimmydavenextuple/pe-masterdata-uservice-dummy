package com.nextuple.jobs.framework.common.domain.pojo;

import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class AuditLog implements Serializable {
  private static final long serialVersionUID = 8075572212366047610L;
  private JobStatusEnum status;
  private Date timeStamp;
}
