package com.nextuple.transit.domain.inbound;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransitDetailsRequest {

  @NotNull @NotEmpty private List<String> destinationGeozones;
}
