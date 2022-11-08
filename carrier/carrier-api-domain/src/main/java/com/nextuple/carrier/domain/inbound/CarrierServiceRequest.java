package com.nextuple.carrier.domain.inbound;

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
public class CarrierServiceRequest implements Serializable {

  @NotBlank(message = "orgId can't be blank")
  @Length(max = 50)
  private String orgId;

  @NotBlank(message = "carrierId can't be blank")
  @Length(max = 50)
  private String carrierId;

  @NotBlank(message = "carrierServiceId can't be blank")
  @Length(max = 50)
  private String carrierServiceId;

  private String carrierName;
  private String serviceName;

  @NotBlank(message = "serviceOptions can't be blank")
  private String serviceOptions;
}
