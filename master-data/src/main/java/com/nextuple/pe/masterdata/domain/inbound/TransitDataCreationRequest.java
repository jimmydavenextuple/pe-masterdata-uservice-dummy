package com.nextuple.pe.masterdata.domain.inbound;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransitDataCreationRequest implements Serializable {

  @NotBlank
  @Length(max = 50)
  private String orgId;

  @NotBlank
  @Length(max = 50)
  private String sourceGeozone;

  @NotBlank
  @Length(max = 50)
  private String destinationGeozone;

  @NotBlank
  @Length(max = 50)
  private String carrierServiceId;

  @NotNull private Float transitDays;
}
