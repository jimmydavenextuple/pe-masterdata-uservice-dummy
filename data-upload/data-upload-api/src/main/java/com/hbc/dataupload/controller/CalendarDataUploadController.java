package com.hbc.dataupload.controller;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.service.CalendarDataUploadService;
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
public class CalendarDataUploadController {
  private final CalendarDataUploadService calendarDataUploadService;

  @PostMapping("/calendar")
  public ResponseEntity<BaseResponse<String>> uploadCalendarData(
      @NotBlank @RequestParam String fileUri) throws IOException, CommonServiceException {
    log.debug("Processing upload Calendar Data request");
    try {
      return calendarDataUploadService.uploadCalendarData(fileUri);
    } catch (Exception e) {
      log.error(String.valueOf(e), "Failed to process upload Calendar Data request!");
      throw e;
    }
  }
}
