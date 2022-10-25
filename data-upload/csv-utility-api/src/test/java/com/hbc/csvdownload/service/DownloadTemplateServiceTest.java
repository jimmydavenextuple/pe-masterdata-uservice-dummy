package com.hbc.csvdownload.service;

import com.hbc.csvdownload.common.TestUtil;
import com.hbc.csvdownload.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DownloadTemplateServiceTest {
  @InjectMocks private DownloadTemplateService downloadTemplateService;

  @Test
  void downloadTemplate() throws Exception {
    Exception exception =
        Assertions.assertThrows(
            InvalidTemplateTypeException.class,
            () -> downloadTemplateService.getTemplateData(TestUtil.templateTypeInvalid));
    Assertions.assertNotNull(exception);
  }
}
