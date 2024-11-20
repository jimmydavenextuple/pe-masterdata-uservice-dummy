package com.nextuple.pe.webhook.domain.inbound;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedRequest<T> {

  @NotEmpty(message = "Data feed cannot be empty")
  @Valid
  private List<T> data;
}
