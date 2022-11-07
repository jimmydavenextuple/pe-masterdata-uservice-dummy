package com.hbc.transit.repository;

import com.hbc.transit.domain.entity.TransitBufferReqJobRefEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitBufferReqJobRefRepository
    extends JpaRepository<TransitBufferReqJobRefEntity, Long> {

  List<TransitBufferReqJobRefEntity> findByExtReferenceId(String extReferenceId);
}
