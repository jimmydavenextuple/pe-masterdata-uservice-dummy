package com.hbc.pe.masterdata.calendar.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hbc.calendar.domain.dto.NodeCarrierCalendarCacheKeyDto;
import com.hbc.calendar.domain.inbound.NodeCarrierServiceCalendarRequest;
import com.hbc.calendar.domain.outbound.NodeCarrierServiceCalendarResponse;
import com.hbc.carrier.domain.feign.CarrierFeign;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.node.domain.feign.NodeFeign;
import com.hbc.node.domain.outbound.NodeResponse;
import com.hbc.pe.masterdata.calendar.domain.CalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.NodeCarrierServiceCalendarDomain;
import com.hbc.pe.masterdata.calendar.domain.entity.NodeCarrierServiceCalendarEntity;
import com.hbc.pe.masterdata.calendar.domain.repository.NodeCarrierServiceCalendarRepository;
import com.hbc.pe.masterdata.calendar.exception.CalendarDomainException;
import com.hbc.pe.masterdata.calendar.exception.DateException;
import com.hbc.pe.masterdata.calendar.util.DateValidation;
import com.hbc.pe.masterdata.calendar.util.TestUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class NodeCarrierServiceCalendarServiceTest {

  @Mock private NodeCarrierServiceCalendarDomain nodeCarrierServiceCalendarDomain;
  @Mock private CalendarDomain calendarDomain;
  @Mock private NodeCarrierServiceCalendarRepository nodeCarrierServiceCalendarRepository;
  @Mock private DateValidation dateValidation;
  @Mock private NodeFeign nodeFeign;
  @Mock private CarrierFeign carrierFeign;
  @InjectMocks private NodeCarrierServiceCalendarService nodeCarrierServiceCalendarService;
  @InjectMocks private TestUtil testUtil;

  @BeforeEach
  public void init() {

    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(nodeCarrierServiceCalendarService, "nodeFeign", nodeFeign);
    ReflectionTestUtils.setField(nodeCarrierServiceCalendarService, "carrierFeign", carrierFeign);
  }

  @Test
  void processCreateNodeCarrierServiceCalendarTest()
      throws CalendarDomainException, DateException, CommonServiceException {
    when(nodeCarrierServiceCalendarDomain.saveNodeCarrierServiceCalendarEntity(any()))
        .thenReturn(testUtil.getNodeCarrierServiceCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.TRUE));
    when(carrierFeign.getCarrierServiceListByOrgId(any())).thenReturn(testUtil.getCarrierServiceResponse());
    when(nodeCarrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    NodeCarrierServiceCalendarResponse resp =
        nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
            testUtil.getNodeCarrierServiceCalendarRequest());

    Assertions.assertEquals(TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.getEffectiveDate()));
    Assertions.assertEquals(TestUtil.DESCRIPTION, Objects.requireNonNull(resp.getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  @DisplayName("When node carrier service calendar to be created already exists")
  void createNodeCarrierServiceCalendarTestException()
      throws CalendarDomainException, CommonServiceException {
    NodeCarrierServiceCalendarRequest nodeCarrierServiceCalendarRequest =
        testUtil.getNodeCarrierServiceCalendarRequest();
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.TRUE));
    when(carrierFeign.getCarrierServiceListByOrgId(any())).thenReturn(testUtil.getCarrierServiceResponse());
    when(nodeCarrierServiceCalendarRepository
            .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
                any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    Exception ex =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                    nodeCarrierServiceCalendarRequest));

    Assertions.assertEquals(
        "Node Carrier Service Calendar already exists for the given details", ex.getMessage());
    verify(nodeCarrierServiceCalendarRepository, times(1))
        .findByCalendarIdAndOrgIdAndNodeIdAndCarrierServiceIdAndEffectiveDate(
            any(), any(), any(), any(), any());
    verify(nodeCarrierServiceCalendarDomain, times(0)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInvalidNodeTest() throws CalendarDomainException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(null);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    Assertions.assertEquals(0x1772, exception.getErrorCode());
    verify(nodeCarrierServiceCalendarDomain, times(0)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInactiveNodeTest() throws CalendarDomainException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.FALSE));
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    Assertions.assertEquals(0x1772, exception.getErrorCode());
    verify(nodeCarrierServiceCalendarDomain, times(0)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarNodeResponseInvalidTest()
      throws CalendarDomainException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    BaseResponse<NodeResponse> nodeResponse = testUtil.getNodeDetails(Boolean.TRUE);
    nodeResponse.setSuccess(false);
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(nodeResponse);
    CommonServiceException exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> {
              nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                  testUtil.getNodeCarrierServiceCalendarRequest());
            });
    Assertions.assertEquals(0x1772, exception.getErrorCode());
    verify(nodeCarrierServiceCalendarDomain, times(0)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarInvalidCarrierServiceIdTest() throws CalendarDomainException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.TRUE);
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());
    when(nodeFeign.getNodeDetails(any(), any())).thenReturn(testUtil.getNodeDetails(Boolean.TRUE));
    BaseResponse<List<CarrierServiceResponse>> response = testUtil.getCarrierServiceResponse();
    response.getPayload().get(0).setCarrierServiceId("INVALID");
    when(carrierFeign.getCarrierServiceListByOrgId(any())).thenReturn(response);
    CommonServiceException exception =
            Assertions.assertThrows(
                    CommonServiceException.class,
                    () -> {
                      nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                              testUtil.getNodeCarrierServiceCalendarRequest());
                    });
    Assertions.assertEquals(0x1773, exception.getErrorCode());
    verify(nodeCarrierServiceCalendarDomain, times(0)).saveNodeCarrierServiceCalendarEntity(any());
  }

  @Test
  void processCreateNodeCarrierServiceCalendarWithInvalidDateTest()
      throws CalendarDomainException, DateException {
    when(dateValidation.validateDate(any())).thenReturn(Boolean.FALSE);
    Exception exception =
        Assertions.assertThrows(
            DateException.class,
            () ->
                nodeCarrierServiceCalendarService.processCreateNodeCarrierServiceCalendarResponse(
                    testUtil.getNodeCarrierServiceCalendarRequest()));
    Assertions.assertEquals("Invalid Date", exception.getMessage());
  }

  @Test
  void processValidateCalendarIdTest() throws CalendarDomainException, CommonServiceException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(testUtil.getCalendarEntity());

    nodeCarrierServiceCalendarService.validateCalendarId(TestUtil.CALENDAR_ID, TestUtil.ORG_ID);

    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processValidateCalendarIdTestException() throws CalendarDomainException {
    when(calendarDomain.getCalendar(any(), any())).thenReturn(null);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () ->
                nodeCarrierServiceCalendarService.validateCalendarId(
                    TestUtil.CALENDAR_ID, TestUtil.ORG_ID));
    Assertions.assertEquals(
        "Cannot create a node carrier service calendar as calendarId/orgId is invalid",
        exception.getMessage());
    verify(calendarDomain, times(1)).getCalendar(any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarWithServiceOptionTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(any(), any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            TestUtil.ORG_ID,
            TestUtil.NODE_ID,
            TestUtil.CARRIER_SERVICE_ID,
            Optional.of(TestUtil.SERVICE_OPTION));

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any(), any());
  }

  @Test
  void processGetNodeCarrierServiceCalendarTest() throws CalendarDomainException {
    when(nodeCarrierServiceCalendarDomain.getNodeCarrierServiceCalendar(any(), any(), any()))
        .thenReturn(List.of(testUtil.getNodeCarrierServiceCalendarEntity()));

    List<NodeCarrierServiceCalendarResponse> resp =
        nodeCarrierServiceCalendarService.processGetNodeCarrierServiceCalendar(
            TestUtil.ORG_ID, TestUtil.NODE_ID, TestUtil.CARRIER_SERVICE_ID, Optional.empty());

    Assertions.assertEquals(
        TestUtil.CALENDAR_ID, Objects.requireNonNull(resp.get(0).getCalendarId()));
    Assertions.assertEquals(TestUtil.ORG_ID, Objects.requireNonNull(resp.get(0).getOrgId()));
    Assertions.assertEquals(
        TestUtil.CARRIER_SERVICE_ID, Objects.requireNonNull(resp.get(0).getCarrierServiceId()));
    Assertions.assertEquals(TestUtil.NODE_ID, Objects.requireNonNull(resp.get(0).getNodeId()));
    Assertions.assertEquals(
        TestUtil.EFFECTIVE_DATE, Objects.requireNonNull(resp.get(0).getEffectiveDate()));
    Assertions.assertEquals(
        TestUtil.DESCRIPTION, Objects.requireNonNull(resp.get(0).getDescription()));
    verify(nodeCarrierServiceCalendarDomain, times(1))
        .getNodeCarrierServiceCalendar(any(), any(), any());
  }

  @Test
  void getAllNodeCarrierCalendarCacheKeysTest() throws CalendarDomainException {
    List<NodeCarrierServiceCalendarEntity> nodeCarrierServiceCalendarEntities =
        testUtil.getNodeCarrierServiceCalendarEntityList();

    when(nodeCarrierServiceCalendarDomain.getAllNodeCarrierServiceCalendars(any()))
        .thenReturn(nodeCarrierServiceCalendarEntities);

    List<NodeCarrierCalendarCacheKeyDto> response =
        nodeCarrierServiceCalendarService.getAllNodeCarrierCalendarCacheKeys(2);

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals(
        nodeCarrierServiceCalendarEntities.get(0).getCarrierServiceId(),
        response.get(0).getCarrierServiceId());
    verify(nodeCarrierServiceCalendarDomain, times(1)).getAllNodeCarrierServiceCalendars(any());
  }
}
