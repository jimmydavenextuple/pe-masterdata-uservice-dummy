package com.hbc.transit.service;

import com.hbc.common.context.Logger;
import com.hbc.common.context.LoggerFactory;
import com.hbc.transit.domain.entity.TransitBufferReqJobRefEntity;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.mapper.TransitBufferReqJobRefMapper;
import com.hbc.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
import com.hbc.transit.repository.TransitBufferReqJobRefRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransitBufferReqJobRefService {

  private final TransitBufferReqJobRefRepository transitBufferReqJobRefRepository;

  public static final TransitBufferReqJobRefMapper INSTANCE =
      Mappers.getMapper(TransitBufferReqJobRefMapper.class);

  private static final Logger logger = LoggerFactory.getLogger(TransitBufferReqJobRefService.class);

  public TransitBufferReqJobRefResponse createTransitBufferReqJobRef(
      TransitBufferReqJobRefRequest transitBufferReqJobRefRequest)
      throws TransitBufferReqJobRefDomainException {

    try {
      var entity = INSTANCE.transitBufferReqJobRefDtoToEntity(transitBufferReqJobRefRequest);

      return INSTANCE.toTransitBufferReqJobRefDto(transitBufferReqJobRefRepository.save(entity));
    } catch (Exception e) {
      logger.error("Failed to create transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(
          e.getMessage(),
          transitBufferReqJobRefRequest.getTransitBufferReqId(),
          transitBufferReqJobRefRequest.getExtReferenceId());
    }
  }

  public List<TransitBufferReqJobRefResponse> getTransitBufferReqJobRefByExtReferenceId(String id)
      throws TransitBufferReqJobRefDomainException {

    try {
      List<TransitBufferReqJobRefEntity> transitBufferReqJobRefEntityList =
          transitBufferReqJobRefRepository.findByExtReferenceId(id);

      if (!transitBufferReqJobRefEntityList.isEmpty()) {
        List<TransitBufferReqJobRefResponse> result = new ArrayList<>();
        transitBufferReqJobRefEntityList.forEach(
            entity -> result.add(INSTANCE.toTransitBufferReqJobRefDto(entity)));

        return result;
      } else {
        logger.error("Failed to fetch transit buffer request job reference");
        throw new TransitBufferReqJobRefDomainException(
            "Unable to find transit buffer job references with this ID: " + id, null, id);
      }
    } catch (Exception e) {
      logger.error("Failed to fetch transit buffer request job reference");
      throw new TransitBufferReqJobRefDomainException(e.getMessage(), null, id);
    }
  }
}
