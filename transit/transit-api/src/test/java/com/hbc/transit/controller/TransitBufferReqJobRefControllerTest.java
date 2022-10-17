package com.hbc.transit.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.transit.TestUtil;
import com.hbc.transit.domain.inbound.TransitBufferReqJobRefRequest;
import com.hbc.transit.domain.outbound.TransitBufferReqJobRefResponse;
import com.hbc.transit.exception.TransitBufferReqJobRefDomainException;
import com.hbc.transit.service.TransitBufferReqJobRefService;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TransitBufferReqJobRefControllerTest {

    @InjectMocks private TransitBufferReqJobRefController transitBufferReqJobRefController;
    @InjectMocks private TestUtil testUtil;

    @Mock private TransitBufferReqJobRefService transitBufferReqJobRefService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransitBufferReqJobRefTest() throws TransitBufferReqJobRefDomainException, CommonServiceException {
        TransitBufferReqJobRefRequest transitBufferReqJobRefRequest =
                testUtil.getTransBufferReqJobRefRequest();
        when(transitBufferReqJobRefService.createTransitBufferReqJobRef(any()))
                .thenReturn(testUtil.getTransBufferReqJobRefResponse());

        ResponseEntity<BaseResponse<TransitBufferReqJobRefResponse>> responseEntity =
                transitBufferReqJobRefController.createTransitBufferReqJobRef(transitBufferReqJobRefRequest);

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(
                testUtil.getTransBufferReqJobRefResponse(), responseEntity.getBody().getPayload());
        verify(transitBufferReqJobRefService, times(1)).createTransitBufferReqJobRef(any());
    }

    @Test
    void createTransitBufferReqJobRefTestError() throws TransitBufferReqJobRefDomainException, CommonServiceException {
        TransitBufferReqJobRefRequest transitBufferReqJobRefRequest =
                testUtil.getTransBufferReqJobRefRequest();
        when(transitBufferReqJobRefService.createTransitBufferReqJobRef(any()))
                .thenThrow(new TransitBufferReqJobRefDomainException
                        ("Failed to create transit buffer request job reference",
                                null,
                                transitBufferReqJobRefRequest.getExtReferenceId()));

        TransitBufferReqJobRefDomainException exception =
                assertThrows(
                        TransitBufferReqJobRefDomainException.class,
                        () -> transitBufferReqJobRefController.createTransitBufferReqJobRef(transitBufferReqJobRefRequest));

        Assertions.assertEquals(
                "Failed to create transit buffer request job reference", exception.getMessage());
        Assertions.assertEquals(
                transitBufferReqJobRefRequest.getExtReferenceId(), exception.getExtReferenceId());

        verify(transitBufferReqJobRefService, times(1)).createTransitBufferReqJobRef(any());
    }

    @Test
    void getTransitBufferReqJobRefByExtRefId() throws TransitBufferReqJobRefDomainException, CommonServiceException {
        TransitBufferReqJobRefResponse transitBufferReqJobRefResponse =
                testUtil.getTransBufferReqJobRefResponse();
        List<TransitBufferReqJobRefResponse> dtoList = new ArrayList<>();
        dtoList.add(transitBufferReqJobRefResponse);

        when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(any()))
                .thenReturn(dtoList);

        ResponseEntity<BaseResponse<List<TransitBufferReqJobRefResponse>>> responseEntity =
                transitBufferReqJobRefController.findTransitBufferReqJobRefByExtRefId("1");

        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(
                testUtil.getTransBufferReqJobRefResponse(), responseEntity.getBody().getPayload().stream().findFirst().get());
        verify(transitBufferReqJobRefService, times(1)).getTransitBufferReqJobRefByExtReferenceId(any());
    }

    @Test
    void getTransitBufferReqJobRefByExtRefIdNotExists() throws TransitBufferReqJobRefDomainException, CommonServiceException {

        when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(any()))
                .thenThrow(new TransitBufferReqJobRefDomainException
                        ("Unable to find transit buffer job references with this ID: 1", null, "1"));

        TransitBufferReqJobRefDomainException exception =
                assertThrows(
                        TransitBufferReqJobRefDomainException.class,
                        () -> transitBufferReqJobRefController.findTransitBufferReqJobRefByExtRefId("1"));

        Assertions.assertEquals(
                "Unable to find transit buffer job references with this ID: 1", exception.getMessage());
        Assertions.assertNull(exception.getId());
        Assertions.assertEquals(
                "1", exception.getExtReferenceId());

        verify(transitBufferReqJobRefService, times(1)).getTransitBufferReqJobRefByExtReferenceId(any());
    }

    @Test
    void getTransitBufferReqJobRefByExtRefIdError() throws TransitBufferReqJobRefDomainException, CommonServiceException {

        when(transitBufferReqJobRefService.getTransitBufferReqJobRefByExtReferenceId(any()))
                .thenThrow(new TransitBufferReqJobRefDomainException
                        ("Unable to fetch transit buffer request job reference data", null, "1"));

        TransitBufferReqJobRefDomainException exception =
                assertThrows(
                        TransitBufferReqJobRefDomainException.class,
                        () -> transitBufferReqJobRefController.findTransitBufferReqJobRefByExtRefId("1"));

        Assertions.assertEquals(
                "Unable to fetch transit buffer request job reference data", exception.getMessage());
        Assertions.assertNull(exception.getId());
        Assertions.assertEquals(
                "1", exception.getExtReferenceId());

        verify(transitBufferReqJobRefService, times(1)).getTransitBufferReqJobRefByExtReferenceId(any());
    }
}
