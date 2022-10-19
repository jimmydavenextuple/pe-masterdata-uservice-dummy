package com.hbc.csvdownload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.csvdownload.service.EddComputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class EddComputationUtilityController {

    private final EddComputationService eddComputationService;

    @PostMapping("/edd-computation")
    public ResponseEntity<BaseResponse<String>> uploadEddComputationData(
            @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
            throws IOException, CommonServiceException {
        log.debug("Processing Edd Computation Data request");
        try {
            return eddComputationService.uploadEddCompuationData(fileUri);
        } catch (Exception e) {
            log.error(String.valueOf(e), "Failed to process Edd Computation Data request!");
            throw e;
        }
    }
}
