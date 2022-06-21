package spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.spring.feign.PostalCodeTimezoneFeignImpl;
import com.nextuple.postal.code.timezone.cache.spring.service.PostalCodeTimezoneFeignClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.util.TestUtil;

@ExtendWith(MockitoExtension.class)
class PostalCodeTimezoneFeignClientServiceImplTest {

  @InjectMocks
  private PostalCodeTimezoneFeignClientServiceImpl postalCodeTimezoneFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          PostalCodeTimezoneCacheKey,
          PostalCodeTimezoneCacheValue,
          String,
          BaseResponse<PostalCodeTimezoneDto>>
      mapper;

  @Mock private PostalCodeTimezoneFeignImpl postalCodeTimezoneFeign;

  @Test
  void get() {
    PostalCodeTimezoneCacheKey cacheKey = testUtil.getPostalCodeTimezoneCacheKey();
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    BaseResponse<PostalCodeTimezoneDto> response =
        testUtil.getBaseResponseOfPostalCodeTimezoneDto();

    when(postalCodeTimezoneFeign.getPostalCodeTimezone(
            cacheKey.getOrgId(), cacheKey.getPostalCodePrefix()))
        .thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, postalCodeTimezoneFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    PostalCodeTimezoneCacheKey invalidCacheKey = testUtil.getPostalCodeTimezoneCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(postalCodeTimezoneFeignClientService.get(invalidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
