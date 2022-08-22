package com.hbc.carrier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.hbc.carrier.TestUtil;
import com.hbc.carrier.domain.inbound.CarrierServiceRequest;
import com.hbc.carrier.domain.inbound.CarrierServiceUpdateRequest;
import com.hbc.carrier.domain.outbound.CarrierServiceResponse;
import com.hbc.carrier.domain.pojo.PageProperties;
import com.hbc.carrier.exception.CarrierServiceDomainException;
import com.hbc.carrier.service.CarrierServiceService;
import com.hbc.common.base.PagePayload;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CarrierServiceControllerTest {
  @InjectMocks private CarrierServiceController carrierServiceController;

  @InjectMocks private TestUtil testUtil;

  @Mock private CarrierServiceService carrierServiceService;

  @Mock private PageProperties pageProperties;

  @Test
  void createCarrierServiceTest() throws CarrierServiceDomainException {
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    when(carrierServiceService.createCarrierService(any(CarrierServiceRequest.class)))
        .thenReturn(testUtil.getCarrierServiceResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.createCarrierService(carrierServiceRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).createCarrierService(any());
  }

  @Test
  void createCarrierServiceExceptionTest() throws CarrierServiceDomainException {
    CarrierServiceRequest carrierServiceRequest = testUtil.getCarrierServiceRequest();
    when(carrierServiceService.createCarrierService(any(CarrierServiceRequest.class)))
        .thenThrow(new RuntimeException("Failed to create carrier service"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () -> carrierServiceController.createCarrierService(carrierServiceRequest));
    Assertions.assertEquals("Failed to create carrier service", exception.getMessage());

    verify(carrierServiceService, times(1)).createCarrierService(any());
  }

  @Test
  void getCarrierServiceDetailsTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceResponse CarrierServiceResponse = testUtil.getCarrierServiceResponse();
    when(carrierServiceService.getCarrierServiceDetails(any(), any(), any()))
        .thenReturn(CarrierServiceResponse);

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.getCarrierServiceDetails(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(CarrierServiceResponse, responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).getCarrierServiceDetails(any(), any(), any());
  }

  @Test
  void getCarrierServiceDetailsExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.getCarrierServiceDetails(any(), any(), any()))
        .thenThrow(new RuntimeException("Unable to fetch carrier service details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.getCarrierServiceDetails(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Unable to fetch carrier service details", exception.getMessage());
    verify(carrierServiceService, times(1)).getCarrierServiceDetails(any(), any(), any());
  }

  @Test
  void updateCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    when(carrierServiceService.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenReturn(testUtil.getCarrierServiceUpdateResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.updateCarrierServiceDetails(
            TestUtil.CARRIER_ID,
            TestUtil.CARRIER_SERVICE_ID,
            TestUtil.ORG_ID,
            carrierServiceUpdateRequest);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceUpdateResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1))
        .updateCarrierServiceDetails(anyString(), anyString(), anyString(), any());
  }

  @Test
  void updateCarrierServiceExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    CarrierServiceUpdateRequest carrierServiceUpdateRequest =
        testUtil.getCarrierServiceUpdateRequest();
    when(carrierServiceService.updateCarrierServiceDetails(
            anyString(), anyString(), anyString(), any(CarrierServiceUpdateRequest.class)))
        .thenThrow(new RuntimeException("Unable to update the carrier service details"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.updateCarrierServiceDetails(
                    TestUtil.CARRIER_ID,
                    TestUtil.CARRIER_SERVICE_ID,
                    TestUtil.ORG_ID,
                    carrierServiceUpdateRequest));
    Assertions.assertEquals("Unable to update the carrier service details", exception.getMessage());

    verify(carrierServiceService, times(1))
        .updateCarrierServiceDetails(anyString(), anyString(), anyString(), any());
  }

  @Test
  void deleteCarrierServiceTest() throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.deleteCarrierService(any(), any(), any()))
        .thenReturn(testUtil.getCarrierServiceResponse());

    ResponseEntity<BaseResponse<CarrierServiceResponse>> responseEntity =
        carrierServiceController.deleteCarrierService(
            TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID);

    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertEquals(
        testUtil.getCarrierServiceResponse(), responseEntity.getBody().getPayload());

    verify(carrierServiceService, times(1)).deleteCarrierService(any(), any(), any());
  }

  @Test
  void deleteCarrierServiceExceptionTest()
      throws CarrierServiceDomainException, CommonServiceException {
    when(carrierServiceService.deleteCarrierService(any(), any(), any()))
        .thenThrow(new RuntimeException("Error while deleting carrier service"));

    Exception exception =
        Assertions.assertThrows(
            Exception.class,
            () ->
                carrierServiceController.deleteCarrierService(
                    TestUtil.CARRIER_ID, TestUtil.CARRIER_SERVICE_ID, TestUtil.ORG_ID));
    Assertions.assertEquals("Error while deleting carrier service", exception.getMessage());

    verify(carrierServiceService, times(1)).deleteCarrierService(any(), any(), any());
  }

  @Test
  void getCarrierServiceListWithPaginationSuccessTest()
      throws CarrierServiceDomainException, CommonServiceException {
    List<CarrierServiceResponse> carrierServiceResponseList =
        testUtil.getCarrierServiceResponseList();

    when(carrierServiceService.getCarrierServiceList(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.createPageCarrierServiceResponse(
                2, carrierServiceResponseList, carrierServiceResponseList.size()));

    ResponseEntity<BaseResponse<PagePayload<CarrierServiceResponse>>> response =
        carrierServiceController.getCarrierServiceListWithPagination(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.of(2),
                Optional.of(1),
                Optional.of(TestUtil.SORT_BY),
                Optional.of(TestUtil.SORT_ORDER_DESC)));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        carrierServiceResponseList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        carrierServiceResponseList.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        "", response.getBody().getPayload().getPagination().getNext(), "Next Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getPrevious()),
        "Previous Uri should not be null");

    verify(carrierServiceService, times(1))
        .getCarrierServiceList(any(), any(), any(), any(), any());
  }

  @Test
  void getCarrierServiceListWithPaginationSuccessDefaultTest()
      throws CarrierServiceDomainException, CommonServiceException {
    List<CarrierServiceResponse> carrierServiceResponseList =
        testUtil.getCarrierServiceResponseList();

    when(pageProperties.getPageNo()).thenReturn(1);
    when(pageProperties.getPageSize()).thenReturn(15);
    when(pageProperties.getSortBy()).thenReturn("carrierId");
    when(pageProperties.getSortOrder()).thenReturn("ASC");
    when(carrierServiceService.getCarrierServiceList(any(), any(), any(), any(), any()))
        .thenReturn(
            testUtil.createPageCarrierServiceResponse(
                2, carrierServiceResponseList, carrierServiceResponseList.size()));

    ResponseEntity<BaseResponse<PagePayload<CarrierServiceResponse>>> response =
        carrierServiceController.getCarrierServiceListWithPagination(
            TestUtil.ORG_ID,
            testUtil.getPageParams(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode(), "Success response");
    Assertions.assertEquals(
        2,
        (int) response.getBody().getPayload().getPagination().getTotalPages(),
        "Pagination Total pages");
    Assertions.assertEquals(
        carrierServiceResponseList.size(),
        (int) response.getBody().getPayload().getPagination().getTotalRecords(),
        "Total Elements");
    Assertions.assertEquals(
        1,
        (int) response.getBody().getPayload().getPagination().getCurrentPage(),
        "Current page number");
    Assertions.assertEquals(
        carrierServiceResponseList.size(),
        response.getBody().getPayload().getData().size(),
        "Paginated data");
    Assertions.assertEquals(
        "",
        response.getBody().getPayload().getPagination().getPrevious(),
        "Previous Uri should be empty");
    Assertions.assertEquals(
        Boolean.TRUE,
        Objects.nonNull(response.getBody().getPayload().getPagination().getNext()),
        "Next Uri should not be null");

    verify(carrierServiceService, times(1))
        .getCarrierServiceList(any(), any(), any(), any(), any());
  }
}
