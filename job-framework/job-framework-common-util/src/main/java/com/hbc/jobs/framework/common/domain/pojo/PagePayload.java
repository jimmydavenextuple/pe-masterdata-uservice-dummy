package com.hbc.jobs.framework.common.domain.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagePayload<T> {
  private List<T> data;
  private Pagination pagination;
  private List<Object> aggregation;

  @Data
  public static class Pagination {
    private String next;
    private String previous;
    private Integer totalPages;
    private Integer currentPage;
    private Integer totalRecords;
  }
}
