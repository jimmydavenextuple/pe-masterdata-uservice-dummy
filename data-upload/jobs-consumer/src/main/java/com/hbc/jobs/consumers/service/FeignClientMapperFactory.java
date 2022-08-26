package com.hbc.jobs.consumers.service;

import com.hbc.jobs.framework.common.domain.enums.JobTypeEnum;
import com.hbc.jobs.framework.common.domain.enums.MasterDataModule;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FeignClientMapperFactory {
  private final TransitMapper transitMapper;
  private final NodeCarrierMapper nodeCarrierMapper;

  public FeignClientMapper getMapper(JobTypeEnum jobTypeEnum) {
    if (jobTypeEnum.getModule() == MasterDataModule.TRANSIT) {
      transitMapper.setJobTypeEnum(jobTypeEnum);
      return transitMapper;
    } else if (jobTypeEnum.getModule() == MasterDataModule.NODE_CARRIER) {
      nodeCarrierMapper.setJobTypeEnum(jobTypeEnum);
      return nodeCarrierMapper;
    }
    return null;
  }
}
