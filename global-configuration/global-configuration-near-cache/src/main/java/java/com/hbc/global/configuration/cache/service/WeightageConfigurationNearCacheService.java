package java.com.hbc.global.configuration.cache.service;

import com.hbc.core.cache.service.GenericNearCacheService;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheKey;
import com.hbc.weightage.configuration.cache.domain.WeightageConfigurationCacheValue;

public interface WeightageConfigurationNearCacheService
    extends GenericNearCacheService<
        WeightageConfigurationCacheKey, WeightageConfigurationCacheValue> {}
