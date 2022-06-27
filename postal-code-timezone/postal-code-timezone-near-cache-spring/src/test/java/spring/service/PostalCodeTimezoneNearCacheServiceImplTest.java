package spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nextuple.core.cache.domain.CacheValue;
import com.nextuple.core.cache.service.GenericFeignCacheService;
import com.nextuple.core.spring.service.AbstractGenericFeignClientServiceImpl;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.service.PostalCodeTimezoneFeignClientServiceImpl;
import com.nextuple.postal.code.timezone.cache.spring.service.PostalCodeTimezoneNearCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.util.TestUtil;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimezoneNearCacheServiceImplTest {

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @InjectMocks
  private PostalCodeTimezoneNearCacheServiceImpl postalCodeTimezoneNearCacheServiceImpl;

  @InjectMocks private TestUtil testUtil;

  @Mock private PostalCodeTimezoneFeignClientServiceImpl postalCodeTimezoneFeignClientService;

  @Mock
  private GenericFeignCacheService<PostalCodeTimezoneCacheKey, PostalCodeTimezoneCacheValue>
      feignCacheService;

  @Test
  void getTestForValidParameters() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();

    Mockito.when(feignCacheService.get(any())).thenReturn(cacheValue);
    AbstractGenericFeignClientServiceImpl abstractGenericSpringLocalCacheService =
        Mockito.mock(AbstractGenericFeignClientServiceImpl.class, Mockito.RETURNS_MOCKS);
    Mockito.when(abstractGenericSpringLocalCacheService.get(any())).thenReturn(cacheValue);

    // First Invocation
    CacheValue cacheValue1 = postalCodeTimezoneNearCacheServiceImpl.get(cacheKey);
    assertEquals(cacheValue, cacheValue1);
    // Second Invocation
    CacheValue cacheValue2 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue2);
    // Third Invocation
    CacheValue cacheValue3 = abstractGenericSpringLocalCacheService.get(cacheKey);
    assertEquals(cacheValue, cacheValue3);
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void getTestForInValidParameters() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();

    Mockito.when(feignCacheService.get(any())).thenReturn(null);
    assertNull(postalCodeTimezoneNearCacheServiceImpl.get(cacheKey));
    verify(feignCacheService, times(1)).get(cacheKey);
  }

  @Test
  void deleteTest() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();
    postalCodeTimezoneNearCacheServiceImpl.delete(cacheKey);
    CacheValue cacheValue = postalCodeTimezoneNearCacheServiceImpl.get(cacheKey);
    assertNull(cacheValue);
  }

  @Test
  void deleteAllTest() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();
    postalCodeTimezoneNearCacheServiceImpl.deleteAll();
    CacheValue cacheValue = postalCodeTimezoneNearCacheServiceImpl.get(cacheKey);
    assertNull(cacheValue);
  }
}
