package com.nextuple.weightage.configuration.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.nextuple.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;

public interface WeightageConfigurationNearCacheService
    extends GenericNearCacheService<
        WeightageConfigurationCacheKey, WeightageConfigurationCacheValue> {}
