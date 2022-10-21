package com.hbc.csvdownload.service;

import com.hbc.csvdownload.exception.InvalidTemplateTypeException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadTemplateService {

  public InputStream getTemplateData(String templateType) throws InvalidTemplateTypeException {
    try {
      var cs = new ClassPathResource("/static/templates/" + templateType.toLowerCase() + ".csv");
      return cs.getInputStream();
    } catch (Exception e) {
      throw new InvalidTemplateTypeException("Invalid template type", templateType);
    }
  }
}
