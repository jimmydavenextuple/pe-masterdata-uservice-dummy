package com.hbc.common.configuration.cache.domain;

import com.hbc.core.cache.domain.CacheValue;

public abstract class AbstractCacheValue implements CacheValue {
  private boolean isDummy = false;

  public boolean isDummy() {
    return isDummy;
  }

  public void setDummy(boolean dummy) {
    this.isDummy = dummy;
  }
}
