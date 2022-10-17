package com.hbc.transit.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.entity.TransitBufferReqJobRefEntity;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
import com.hbc.transit.repository.TransitBufferReqJobRefRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransitBufferReqJobRefServiceTest {

    @InjectMocks private TransitBufferReqJobRefService transitBufferReqJobRefService;
    @InjectMocks private TestUtil testUtil;

    @Mock private TransitBufferReqJobRefRepository transitBufferReqJobRefRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransitBufferReqJobRefTest() throws TransitBufferReqJobRefDomainException, CommonServiceException {
        TransitBufferReqJobRefRequest transitBufferReqJobRefRequest =
                testUtil.getTransBufferReqJobRefRequest();
        when(transitBufferReqJobRefRepository.save(any()))
                .thenReturn(testUtil.getTransitBufferReqJobRefEntity());

        TransitBufferReqJobRefResponse responseEntity =
                transitBufferReqJobRefService.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);

        Assertions.assertEquals(
                testUtil.getTransBufferReqJobRefResponse().getExtReferenceId(), responseEntity.getExtReferenceId());
        verify(transitBufferReqJobRefRepository, times(1)).save(any());
    }

    @Test
    void getTransitBufferReqJobRefByExtRefId() throws TransitBufferReqJobRefDomainException, CommonServiceException {
        TransitBufferReqJobRefEntity transitBufferReqJobRef =
                testUtil.getTransitBufferReqJobRefEntity();
        List<TransitBufferReqJobRefEntity> entityList = new ArrayList<TransitBufferReqJobRefEntity>();
        entityList.add(transitBufferReqJobRef);

        when(transitBufferReqJobRefRepository.findByExtReferenceId(any()))
                .thenReturn(entityList);

        List<TransitBufferReqJobRefResponse> responseEntity =
                transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId("1");

        Assertions.assertEquals(
                testUtil.getTransBufferReqJobRefResponse().getExtReferenceId(), responseEntity.stream().findFirst().get().getExtReferenceId());
        verify(transitBufferReqJobRefRepository, times(1)).findByExtReferenceId(any());
    }

    @Test
    void getTransitBufferReqJobRefByExtRefIdNotExists() throws TransitBufferReqJobRefDomainException, CommonServiceException {

        List<TransitBufferReqJobRefEntity> entityList = new ArrayList<>();
        entityList.clear();

        when(transitBufferReqJobRefRepository.findByExtReferenceId(any()))
                .thenReturn(entityList);

        TransitBufferReqJobRefDomainException exception =
                assertThrows(
                        TransitBufferReqJobRefDomainException.class,
                        () -> transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId("1"));

        Assertions.assertEquals(
                "Unable to find transit buffer job references with this ID: 1", exception.getMessage());
        Assertions.assertNull(exception.getId());
        Assertions.assertEquals(
                "1", exception.getExtReferenceId());

        verify(transitBufferReqJobRefRepository, times(1)).findByExtReferenceId(any());
    }

}
