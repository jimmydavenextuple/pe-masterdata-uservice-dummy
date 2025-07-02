package com.nextuple.postalcodecarrierservice.data.cache.service;

import com.nextuple.core.cache.service.GenericNearCacheService;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheKey;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheValue;

public interface PostalCodeCarrierNearCacheService
    extends GenericNearCacheService<
        PostalCodeCarrierServiceDataCacheKey, PostalCodeCarrierServiceDataCacheValue> {}
