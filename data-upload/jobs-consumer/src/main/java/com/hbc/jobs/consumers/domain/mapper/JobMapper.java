package com.hbc.jobs.consumers.domain.mapper;

import com.hbc.jobs.consumers.domain.entity.JobEntity;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapper {

  JobMapper INSTANCE = Mappers.getMapper(JobMapper.class);

  JobDto toJob(JobEntity jobEntity);

  JobEntity toJobEntity(JobDto jobDto);

  List<JobDto> toJobList(List<JobEntity> jobEntities);

  List<JobEntity> toJobEntityList(List<JobDto> jobDtos);

  JobEntity updateJobEntity(JobDto jobDto, @MappingTarget JobEntity oldJobEntity);
}
