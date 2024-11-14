package com.nextuple.core.cache.service;

public interface GenericFeignService<I, O> {

  O get(I request);
}
