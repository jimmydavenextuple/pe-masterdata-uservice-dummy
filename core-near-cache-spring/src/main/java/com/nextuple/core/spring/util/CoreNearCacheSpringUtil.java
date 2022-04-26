package com.nextuple.core.spring.util;

import java.util.concurrent.TimeUnit;

public class CoreNearCacheSpringUtil {

  private static final String REMOTE_CACHE_NAME = "remoteCacheName";
  private static final Long REMOTE_CACHE_TTL_HOURS = 24l;
  private static final Long NEAR_CACHE_MAX_SIZE = 1000l;
  private static final Long NEAR_CACHE_DURATION = 60l;
  private static final TimeUnit NEAR_CACHE_TIME_UNIT = TimeUnit.SECONDS;

  private CoreNearCacheSpringUtil() {}

  public static String getRemoteCacheName(String cacheName) {
    if (cacheName.length() == 0 || cacheName == null) {
      return REMOTE_CACHE_NAME;
    }
    return cacheName;
  }

  public static Long getRemoteCacheTTL(Long ttl) {
    if (ttl == null || ttl == 0) {
      return REMOTE_CACHE_TTL_HOURS;
    }
    return ttl;
  }

  public static Long getNearCacheMaxSize(Long maxSize) {
    if (maxSize == null || maxSize == 0) {
      return NEAR_CACHE_MAX_SIZE;
    }
    return maxSize;
  }

  public static Long getNearCacheDuration(Long duration) {
    if (duration == null || duration == 0) {
      return NEAR_CACHE_DURATION;
    }
    return duration;
  }

  public static TimeUnit getNearCacheTimeUnit(TimeUnit timeUnit) {
    if (timeUnit == null) {
      return NEAR_CACHE_TIME_UNIT;
    }
    return timeUnit;
  }
}
