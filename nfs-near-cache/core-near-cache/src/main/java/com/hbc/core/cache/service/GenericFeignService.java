package com.hbc.core.cache.service;

public interface GenericFeignService<I, O> {

  O get(I request);
}
