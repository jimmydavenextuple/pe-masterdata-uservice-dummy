package com.hbc.common.pojo;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {

  private Optional<Integer> pageNo = Optional.empty();
  private Optional<Integer> pageSize = Optional.empty();
  private Optional<String> sortBy = Optional.empty();
  private Optional<String> sortOrder = Optional.empty();
}
