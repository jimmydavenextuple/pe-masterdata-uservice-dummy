package com.nextuple.jobs.framework.common.domain.pojo;

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
  private Optional<String> sortBy = Optional.empty();
  private Optional<String> sortOrder = Optional.empty();
  private Optional<Integer> pageNo = Optional.empty();
  private Optional<Integer> pageSize = Optional.empty();
}
