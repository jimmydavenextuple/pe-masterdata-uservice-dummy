package com.nextuple.pe.webhook.domain.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InventoryATP {

  private String orgId;

  @NotBlank(message = "itemId cannot be empty or null")
  private String itemId;

  @NotBlank(message = "unitOfMeasure cannot be empty or null")
  private String unitOfMeasure;

  @NotBlank(message = "nodeId cannot be empty or null")
  private String nodeId;

  private String inventoryClass;

  @NotBlank(message = "deliveryMethod cannot be empty or null")
  private String deliveryMethod;

  @NotNull(message = "onhandAvailableQuantity cannot be empty or null")
  @Min(value = 0, message = "onhandAvailableQuantity must be greater than or equal to 0")
  private double onhandAvailableQuantity;

  @NotNull(message = "computeTS cannot be empty or null")
  private long computeTs;

  /* optional attributes */

  @Min(value = 0)
  private double futureAvailableQuantity;

  @Min(value = 0)
  private long earliestFutureAvailableDate;

  @Min(value = 0)
  private long eventTime;
}
