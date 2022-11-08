package com.nextuple.core.event;

import com.nextuple.core.base.BaseEvent;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalCacheUpdateEvent extends BaseEvent implements Serializable {
  private static final long serialVersionUID = 7486058354271542521L;
  LocalCacheUpdateMessage localCacheUpdateMessage;
}
