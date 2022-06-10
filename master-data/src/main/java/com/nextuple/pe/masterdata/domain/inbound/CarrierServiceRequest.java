package com.nextuple.pe.masterdata.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrierServiceRequest implements Serializable {

  @NotBlank
  @Length(max = 50)
  private String orgId;

  @NotBlank
  @Length(max = 50)
  private String carrierId;

  @NotBlank
  @Length(max = 50)
  private String serviceId;

  private String carrierName;
  private String serviceName;

  @NotBlank private String serviceOptions;
}
