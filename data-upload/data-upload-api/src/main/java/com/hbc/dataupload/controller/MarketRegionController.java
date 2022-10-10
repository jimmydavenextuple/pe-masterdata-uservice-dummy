package com.hbc.dataupload.controller;

import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.MarketRegionService;
import com.hbc.postal.code.timezone.api.domain.dto.MarketRegionDto;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/ui/regions-nodes")
@RequiredArgsConstructor
@Slf4j
public class MarketRegionController {
  private final MarketRegionService marketRegionService;

  @GetMapping("/market-regions/orgId/{orgId}")
  public ResponseEntity<BaseResponse<List<MarketRegionDto>>> getMarketRegions(
      @NotBlank(message = "OrgId can't be empty") @PathVariable String orgId) {
    return ResponseEntity.ok(
        BaseResponse.builder()
            .message("Market Regions fetched successfully")
            .payload(marketRegionService.getMarketRegions(orgId))
            .build());
  }
}
