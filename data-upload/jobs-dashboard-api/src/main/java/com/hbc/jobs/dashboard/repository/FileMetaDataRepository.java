package com.hbc.jobs.dashboard.repository;

import com.hbc.jobs.dashboard.domain.entity.FileMetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetaDataRepository extends JpaRepository<FileMetaDataEntity, Long> {}
