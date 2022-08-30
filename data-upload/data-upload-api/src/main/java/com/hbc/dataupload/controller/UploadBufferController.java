package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.UploadBufferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class UploadBufferController {
    private final UploadBufferService uploadBufferService;

    @PostMapping("/node-service-option-buffer")
    public ResponseEntity<BaseResponse<String>> uploadNodeServiceOptionBufferData(
            @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
        log.debug("Processing upload Node ServiceOption Buffer Data request");
        try {
            return uploadBufferService.uploadNodeServiceOptionBufferData(fileUri);
        } catch (Exception e) {
            log.error(String.valueOf(e), "Failed to process upload Node ServiceOption Buffer Data request!");
            throw e;
        }
    }

    @PostMapping("/transit-buffer")
    public ResponseEntity<BaseResponse<String>> uploadTransitBufferData(
            @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
        log.debug("Processing upload Transit Buffer Data request");
        try {
            return uploadBufferService.uploadTransitBufferData(fileUri);
        } catch (Exception e) {
            log.error(String.valueOf(e), "Failed to process upload Transit Buffer Data request!");
            throw e;
        }
    }
}