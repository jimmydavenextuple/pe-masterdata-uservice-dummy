package com.nextuple.promise.sourcing.rule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextuple.common.exception.CommonServiceException;
import com.nextuple.common.exception.PromiseEngineException;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.promise.sourcing.rule.api.domain.inbound.AttributeValueRequest;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AddAttributeValueResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.AttributeValueResponse;
import com.nextuple.promise.sourcing.rule.service.AttributeService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AttributesControllerTest {

  @Mock private AttributeService attributeService;

  @InjectMocks private AttributesController attributesController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveValueToAttributeTest() throws PromiseEngineException, CommonServiceException {
    AddAttributeValueResponse attributeValueResponse =
        AddAttributeValueResponse.builder().attributeName("V1").value("abc").build();
    AttributeValueRequest attributeValueRequest = new AttributeValueRequest("SDND");
    when(attributeService.addValueToAttribute(
            anyString(), anyString(), any(AttributeValueRequest.class)))
        .thenReturn(attributeValueResponse);

    ResponseEntity<BaseResponse<AddAttributeValueResponse>> responseEntity =
        attributesController.saveValueToAttribute(
            "NEXTUPLE", "SERVICEOPTION", attributeValueRequest);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(attributeValueResponse, responseEntity.getBody().getPayload());
    verify(attributeService, times(1))
        .addValueToAttribute(anyString(), anyString(), any(AttributeValueRequest.class));
  }

  @Test
  void getAttributeValueTest() throws PromiseEngineException, CommonServiceException {
    AttributeValueResponse attributeValueResponse =
        AttributeValueResponse.builder()
            .attributeName("SERVICEOPTION")
            .values(new ArrayList<>())
            .build();
    when(attributeService.getAttributeValues(anyString(), anyString()))
        .thenReturn(attributeValueResponse);

    ResponseEntity<BaseResponse<AttributeValueResponse>> responseEntity =
        attributesController.getAttributeValues("NEXTUPLE", "SERVICEOPTION");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(attributeValueResponse, responseEntity.getBody().getPayload());
    verify(attributeService, times(1)).getAttributeValues(anyString(), anyString());
  }

  @Test
  void deleteAttributeValueTest() throws PromiseEngineException, CommonServiceException {
    AddAttributeValueResponse attributeValueResponse =
        AddAttributeValueResponse.builder().attributeName("V1").value("abc").build();

    when(attributeService.deleteValueOfAttribute(anyString(), anyString(), anyString()))
        .thenReturn(attributeValueResponse);

    ResponseEntity<BaseResponse<AddAttributeValueResponse>> responseEntity =
        attributesController.deleteAttributeValue("NEXTUPLE", "SERVICEOPTION", "SDND");

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(attributeValueResponse, responseEntity.getBody().getPayload());
    verify(attributeService, times(1))
        .deleteValueOfAttribute(anyString(), anyString(), anyString());
  }
}
