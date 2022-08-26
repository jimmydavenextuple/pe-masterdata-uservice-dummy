package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.jobs.consumers.domain.entity.JobRecordEntity;
import com.hbc.jobs.framework.common.domain.pojo.RecordStatusDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobRecordMapper {

  JobRecordMapper INSTANCE = Mappers.getMapper(JobRecordMapper.class);

  RecordStatusDto toRecordStatus(JobRecordEntity jobRecordEntity);

  JobRecordEntity toJobRecordEntity(RecordStatusDto recordStatusDto);

  List<RecordStatusDto> toRecordStatusDtoList(List<JobRecordEntity> jobRecordEntities);

  List<JobRecordEntity> toJobRecordEntityList(List<RecordStatusDto> recordStatusDtos);
}
