package com.nextuple.sourcing.rule.spring.cache.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.core.cache.mapper.GenericMapper;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleRequest;
import com.nextuple.sourcing.rule.cache.domain.FetchPromiseSourcingRuleResponse;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheKey;
import com.nextuple.sourcing.rule.cache.domain.SourcingRuleCacheValue;
import com.nextuple.sourcing.rule.spring.cache.feign.SourcingRuleFeignImpl;
import com.nextuple.sourcing.rule.spring.cache.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SourcingRuleFeignClientServiceImplTest {

  @InjectMocks private SourcingRuleFeignClientServiceImpl sourcingRuleFeignClientService;

  @InjectMocks private TestUtil testUtil;

  @Mock
  private GenericMapper<
          SourcingRuleCacheKey,
          SourcingRuleCacheValue,
          FetchPromiseSourcingRuleRequest,
          BaseResponse<FetchPromiseSourcingRuleResponse>>
      mapper;

  @Mock private SourcingRuleFeignImpl sourcingRuleFeign;

  @Test
  void getTest() {
    SourcingRuleCacheKey cacheKey = testUtil.getSourcingRuleCacheKey();
    SourcingRuleCacheValue cacheValue = testUtil.getSourcingRuleCacheValue();

    BaseResponse<FetchPromiseSourcingRuleResponse> response =
        testUtil.getBaseResponseOfFetchPromiseSourcingRuleResponse();

    when(mapper.cacheKeyToRequest(cacheKey))
        .thenReturn(cacheKey.getFetchPromiseSourcingRuleRequest());
    when(sourcingRuleFeign.get(cacheKey.getFetchPromiseSourcingRuleRequest())).thenReturn(response);
    when(mapper.responseToCacheValue(response)).thenReturn(cacheValue);

    assertEquals(cacheValue, sourcingRuleFeignClientService.get(cacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }

  @Test
  void getForExceptionTest() {
    SourcingRuleCacheKey inValidCacheKey = testUtil.getSourcingRuleCacheKey();

    when(mapper.responseToCacheValue(any())).thenThrow(new RuntimeException("Error message"));
    assertNull(sourcingRuleFeignClientService.get(inValidCacheKey));
    verify(mapper, times(1)).responseToCacheValue(any());
  }
}
