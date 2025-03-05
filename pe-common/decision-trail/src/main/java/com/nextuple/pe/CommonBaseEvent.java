package com.nextuple.pe;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CommonBaseEvent extends BaseEvent {
  /** Custom attributes for the event */
  private JsonNode customAttributes;
}
