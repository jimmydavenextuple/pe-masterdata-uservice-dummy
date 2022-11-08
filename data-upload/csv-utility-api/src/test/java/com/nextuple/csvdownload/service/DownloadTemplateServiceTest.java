package com.nextuple.csvdownload.service;

import com.nextuple.csvdownload.common.TestUtil;
import com.nextuple.csvdownload.exception.*;
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
