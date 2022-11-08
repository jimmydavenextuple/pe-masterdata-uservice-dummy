package com.nextuple.transit.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.nextuple.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.nextuple.transit.exception.TransitBufferReqJobRefDomainException;
import com.nextuple.transit.service.TransitBufferReqJobRefService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transit/transit-buffer-req-jobs-reference")
@RequiredArgsConstructor
public class TransitBufferReqJobRefController {

  private static final Logger logger =
      LoggerFactory.getLogger(TransitBufferReqJobRefController.class);

  private final TransitBufferReqJobRefService transitBufferReqJobRefService;

  @PostMapping
  public ResponseEntity<BaseResponse<TransitBufferReqJobRefResponse>> createTransitBufferReqJobRef(
      @Valid @RequestBody TransitBufferReqJobRefRequest transitBufferReqJobRefRequest)
      throws TransitBufferReqJobRefDomainException {
    logger.debug("Processing transit buffer request job reference creation request");
    try {
      var response =
          transitBufferReqJobRefService.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);
      logger.info("Response after creation of transit buffer request job reference:");
      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("transit buffer request job reference created successfully")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to create transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(
          e.getMessage(),
          transitBufferReqJobRefRequest.getTransitBufferReqId(),
          transitBufferReqJobRefRequest.getExtReferenceId());
    }
  }

  @GetMapping("/{extReferenceId}")
  public ResponseEntity<BaseResponse<List<TransitBufferReqJobRefResponse>>>
      findTransitBufferReqJobRefByExtRefId(
          @NotBlank @PathVariable("extReferenceId") String extReferenceId)
          throws TransitBufferReqJobRefDomainException {
    try {
      var response =
          transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(extReferenceId);

      return ResponseEntity.ok(
          BaseResponse.builder()
              .message("Transit buffer request job reference fetched successfully")
              .payload(response)
              .build());
    } catch (Exception e) {
      logger.error("Failed to get transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(e.getMessage(), null, extReferenceId);
    }
  }
}
