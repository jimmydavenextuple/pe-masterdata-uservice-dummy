package com.hbc.service.inventory.domain.inbound;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceInventoryRequest implements Serializable {
  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "serviceOption can't be blank")
  @Length(max = 50)
  private String serviceOption;

  @NotBlank(message = "inventoryType can't be blank")
  @Length(max = 50)
  private String inventoryType;
}
