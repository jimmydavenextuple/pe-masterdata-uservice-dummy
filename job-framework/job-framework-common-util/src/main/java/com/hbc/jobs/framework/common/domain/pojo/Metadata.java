package com.hbc.jobs.framework.common.domain.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.HashMap;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata extends HashMap<String, String> implements Serializable {
  private static final long serialVersionUID = -7963504948824864639L;
}
