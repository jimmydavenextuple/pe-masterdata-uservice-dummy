package com.nextuple.postal.code.timezone.service;

import com.nextuple.postal.code.timezone.domain.PostalCodeTimezoneDomain;
import com.nextuple.postal.code.timezone.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.domain.entity.PostalCodeTimezoneEntity;
import com.nextuple.postal.code.timezone.domain.inbound.CreatePostalCodeTimezoneRequest;
import com.nextuple.postal.code.timezone.domain.mapper.PostalCodeTimezoneMapper;
import com.nextuple.postal.code.timezone.exception.common.PromiseEngineException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostalCodeTimezoneService {
    private static final Logger logger = LoggerFactory.getLogger(PostalCodeTimezoneService.class);
    private static final PostalCodeTimezoneMapper INSTANCE =
            Mappers.getMapper(PostalCodeTimezoneMapper.class);
    private final PostalCodeTimezoneDomain postalCodeTimezoneDomain;

    /**
     * Convert PostalCodeTimezone Entity to PostalCodeTimezone Dto with all required processing
     *
     * @param postalCodeTimezoneEntity PostalCodeTimezone Entity
     * @return PostalCodeTimezoneDto dto
     */
    private PostalCodeTimezoneDto preparePostalCodeTimezoneDto(
            PostalCodeTimezoneEntity postalCodeTimezoneEntity) {
        return INSTANCE.convertToPostalCodeTimezoneDto(postalCodeTimezoneEntity);
    }

    /**
     * Creates new PostalCodeTimezone entity
     *
     * @param baseRequest for Creating Postal Code Timezone
     * @return Created Postal Code Timezone dto
     * @throws PromiseEngineException
     */
    public PostalCodeTimezoneDto createPostalCodeTimezone(CreatePostalCodeTimezoneRequest baseRequest)
            throws PromiseEngineException {
        logger.info("-- inside createPostalCodeTimezone service --");
        PostalCodeTimezoneEntity postalCodeTimezoneEntity =
                INSTANCE.convertFromCreatePostalCodeTimezoneRequestToEntity(baseRequest);
        return preparePostalCodeTimezoneDto(
                postalCodeTimezoneDomain.savePostalCodeTimezone(postalCodeTimezoneEntity));
    }
}
