package com.nextuple.jobs.dashboard.repository;

import com.nextuple.jobs.dashboard.domain.entity.FileMetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetaDataRepository extends JpaRepository<FileMetaDataEntity, Long> {}
