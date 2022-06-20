package spring.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheKey;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneCacheValue;
import com.nextuple.postal.code.timezone.cache.domain.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.cache.spring.mapper.PostalCodeTimezoneMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.util.TestUtil;

@ExtendWith(MockitoExtension.class)
public class PostalCodeTimezoneMapperTest {
  @InjectMocks
  private GenericMapper<
          PostalCodeTimezoneCacheKey,
          PostalCodeTimezoneCacheValue,
          String,
          BaseResponse<PostalCodeTimezoneDto>>
      genericMapper = new PostalCodeTimezoneMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(genericMapper.requestToCacheKey("request"));
  }

  @Test
  void cacheKeyToRequest() {
    assertNull(genericMapper.cacheKeyToRequest(testUtil.getPostalCodeTimezoneCacheKey()));
  }

  @Test
  void responseToCacheValue() {
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    BaseResponse<PostalCodeTimezoneDto> response =
        testUtil.getBaseResponseOfPostalCodeTimezoneDto();

    assertEquals(
        cacheValue.getPostalCodeTimezoneDto().getOrgId(),
        genericMapper.responseToCacheValue(response).getPostalCodeTimezoneDto().getOrgId());
    assertEquals(
        cacheValue.getPostalCodeTimezoneDto().getPostalCodePrefix(),
        genericMapper
            .responseToCacheValue(response)
            .getPostalCodeTimezoneDto()
            .getPostalCodePrefix());
  }

  @Test
  void responseToCacheValueNullTest() {
    PostalCodeTimezoneCacheValue cacheValue =
        PostalCodeTimezoneCacheValue.builder().postalCodeTimezoneDto(null).build();

    BaseResponse<PostalCodeTimezoneDto> response = new BaseResponse<>();
    response.setPayload(null);

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    PostalCodeTimezoneCacheValue cacheValue = testUtil.getPostalCodeTimezoneCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
