package com.nextuple.jobs.framework.common.domain.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.CollectionUtils;

public class JobReferences extends HashMap<String, Object> implements Serializable {

  private static final long serialVersionUID = 1738297641544942023L;

  public JobReferences() {}

  public JobReferences(Map<String, Object> jobReferences) {
    if (!CollectionUtils.isEmpty(jobReferences)) this.putAll(jobReferences);
  }
}
