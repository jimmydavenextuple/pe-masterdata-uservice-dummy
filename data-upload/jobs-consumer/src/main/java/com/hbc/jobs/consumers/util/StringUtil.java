package com.hbc.jobs.consumers.util;

import com.hbc.common.util.JsonUtil;
import com.hbc.csvdownload.exception.JsonParsingException;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {

  private StringUtil() {}

  public static String createJobRequest(List<?> requestList, String orgId)
      throws JsonParsingException {
    log.debug("Inside create job request method");
    String jobRequest;
    try {
      Function<List<?>, String> dataMapper = request -> JsonUtil.convert(requestList);
      jobRequest = dataMapper.apply(requestList);
    } catch (Exception e) {
      log.error("Error in parsing Job Json String", e);
      throw new JsonParsingException("Error in parsing Job Json String", e, orgId);
    }
    return jobRequest;
  }
}
