package com.nextuple.postalcodecarrierservice.spring.cache.service;

import com.nextuple.core.constants.NearCacheConstants;
import com.nextuple.core.registry.NearCacheRegistry;
import com.nextuple.core.spring.service.AbstractGenericSpringLocalCacheServiceImpl;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheKey;
import com.nextuple.postalcodecarrierservice.data.cache.domain.PostalCodeCarrierServiceDataCacheValue;
import com.nextuple.postalcodecarrierservice.data.cache.service.PostalCodeCarrierNearCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(
    cacheNames = PostalCodeCarrierNearCacheServiceImpl.POSTAL_CODE_CARRIER_SERVICE_CACHE_NAME)
@RequiredArgsConstructor
public class PostalCodeCarrierNearCacheServiceImpl
    extends AbstractGenericSpringLocalCacheServiceImpl<
        PostalCodeCarrierServiceDataCacheKey, PostalCodeCarrierServiceDataCacheValue>
    implements PostalCodeCarrierNearCacheService {
  private static final Logger logger =
      LoggerFactory.getLogger(PostalCodeCarrierNearCacheServiceImpl.class);
  public static final String POSTAL_CODE_CARRIER_SERVICE_CACHE_NAME = "postal_code_carrier_service";
  private final NearCacheRegistry nearCacheRegistry;

  @Override
  public String getCacheName() {
    return PostalCodeCarrierNearCacheServiceImpl.POSTAL_CODE_CARRIER_SERVICE_CACHE_NAME;
  }

  @PostConstruct
  @Override
  public void selfRegister() {
    nearCacheRegistry.registerNearCacheEntity(
        NearCacheConstants.POSTAL_CODE_CARRIER_SERVICE_ENTITY_NAME,
        PostalCodeCarrierServiceDataCacheKey.class.getName(),
        "partial");
  }

  @Override
  public String getEntityName() {
    return NearCacheConstants.POSTAL_CODE_CARRIER_SERVICE_ENTITY_NAME;
  }
}
