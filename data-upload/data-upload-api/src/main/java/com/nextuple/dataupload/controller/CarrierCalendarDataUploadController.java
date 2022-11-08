package com.nextuple.dataupload.controller;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.dataupload.service.CarrierCalendarDataUploadService;
import java.io.IOException;
import javax.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/upload")
@Slf4j
@RequiredArgsConstructor
public class CarrierCalendarDataUploadController {

  private final CarrierCalendarDataUploadService calendarDataUploadService;

  @PostMapping("/carrier-service-calendar")
  public ResponseEntity<BaseResponse<String>> uploadCarrierCalendarData(
      @NotBlank(message = "fileUri can't be empty") @RequestParam String fileUri)
      throws IOException, CommonServiceException {
    log.debug("Processing upload Carrier Calendar Data request");
    try {
      return calendarDataUploadService.uploadCarrierCalendarData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Carrier Calendar Data request!");
      throw e;
    }
  }
}
