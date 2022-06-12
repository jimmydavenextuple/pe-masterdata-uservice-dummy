package com.nextuple.postal.code.timezone.controller;

import com.nextuple.common.response.BaseResponse;
import com.nextuple.postal.code.timezone.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import com.nextuple.postal.code.timezone.service.PostalCodeTimezoneService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/postalCodeTimezone")
@RequiredArgsConstructor
public class PostalCodeTimezoneController {

    private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneController.class);
    private final PostalCodeTimezoneService postalCodeTimezoneService;

    @PostMapping
    public ResponseEntity<BaseResponse<PostalCodeTimezoneDto>> createPostalCodeTimezone(
            @Valid @RequestBody CreatePostalCodeTimezoneRequest baseRequest)
            throws PromiseEngineException {
        logger.info("Processing create Postal Code Timezone request");
        try {
            return ResponseEntity.ok(
                    BaseResponse.builder()
                            .message("Postal Code Timezone successfully created!")
                            .payload(postalCodeTimezoneService.createPostalCodeTimezone(baseRequest))
                            .build());
        } catch (Exception e) {
            logger.error(String.valueOf(e), "Failed to process create Postal Code Timezone request!");
            throw e;
        }
    }
}
