package com.hbc.jobs.framework.common.domain.pojo;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobFilters {
  private Optional<String> jobType = Optional.empty();
  private Optional<Integer> days = Optional.empty();
  private String sortBy = "created_date";
  private String sortOrder = "ASC";
  private Integer pageNo = 1;
  private Integer pageSize = 15;
}
