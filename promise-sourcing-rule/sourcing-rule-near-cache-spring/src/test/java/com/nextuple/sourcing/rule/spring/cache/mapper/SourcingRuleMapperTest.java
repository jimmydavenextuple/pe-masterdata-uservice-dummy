package com.nextuple.sourcing.rule.spring.cache.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.FetchPromiseSourcingRuleRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.FetchPromiseSourcingRuleResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingRuleMapperTest {

  @InjectMocks
  private GenericMapper<
          SourcingRuleCacheKey,
          SourcingRuleCacheValue,
          FetchPromiseSourcingRuleRequest,
          BaseResponse<FetchPromiseSourcingRuleResponse>>
      genericMapper = new SourcingRuleMapper();

  @InjectMocks private TestUtil testUtil;

  @Test
  void requestToCacheKey() {
    assertNull(
        genericMapper.requestToCacheKey(
            testUtil.getFetchPromiseSourcingRuleRequest("ABC", List.of("SDND"), "AOA", "DEFAULT")));
  }

  @Test
  void cacheKeyToRequest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    assertEquals(
        cacheKey.getFetchPromiseSourcingRuleRequest(), genericMapper.cacheKeyToRequest(cacheKey));
  }

  @Test
  void responseToCacheValue() {
    SourcingRuleCacheValue cacheValue = testUtil.getSourcingRuleCacheValue();
    BaseResponse<FetchPromiseSourcingRuleResponse> response =
        testUtil.getBaseResponseOfFetchPromiseSourcingRuleResponse();

    assertEquals(cacheValue, genericMapper.responseToCacheValue(response));
  }

  @Test
  void cacheValueToResponse() {
    SourcingRuleCacheValue cacheValue = testUtil.getSourcingRuleCacheValue();
    assertNull(genericMapper.cacheValueToResponse(cacheValue));
  }
}
