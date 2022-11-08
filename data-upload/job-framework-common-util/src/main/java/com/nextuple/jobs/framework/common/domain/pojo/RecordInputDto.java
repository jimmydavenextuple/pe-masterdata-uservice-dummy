package com.nextuple.jobs.framework.common.domain.pojo;

import java.io.Serializable;
import java.util.HashMap;
import lombok.Data;

@Data
public class RecordInputDto extends HashMap<String, Serializable> implements Serializable {
  private static final long serialVersionUID = 3730446649043884186L;
}
