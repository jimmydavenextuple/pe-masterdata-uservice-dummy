package com.nextuple.core.consumer;

import com.nextuple.core.base.BaseEvent;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalCacheUpdateEvent extends BaseEvent implements Serializable {

  LocalCacheUpdateMessage localCacheUpdateMessage;
}
