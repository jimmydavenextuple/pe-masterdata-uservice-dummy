package com.nextuple.pe.light.promise.service.impl;

import com.nextuple.common.context.Logger;
import com.nextuple.common.context.LoggerFactory;
import com.nextuple.core.cache.service.GenericNearCacheService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheWarmUpService {
  private static final Logger logger = LoggerFactory.getLogger(CacheWarmUpService.class);

  @Autowired List<GenericNearCacheService> nearCacheServices; // NOSONAR

  public void deleteAllNearCacheData() {
    for (GenericNearCacheService genericNearCacheService : nearCacheServices) { // NOSONAR
      try {
        genericNearCacheService.deleteAll();
      } catch (Exception e) {
        logger.error("Exception while cache evict for " + genericNearCacheService.getEntityName());
      }
    }
  }
}
