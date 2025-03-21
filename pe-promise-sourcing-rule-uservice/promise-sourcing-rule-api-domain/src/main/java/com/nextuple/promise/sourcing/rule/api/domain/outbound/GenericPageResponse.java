package com.nextuple.promise.sourcing.rule.api.domain.outbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericPageResponse {
    GenericDetailsResponse data;
    GenericPaginationAttribute pagination;
}
