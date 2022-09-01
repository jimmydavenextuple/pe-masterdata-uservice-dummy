package com.hbc.dataupload.service;

import static com.hbc.dataupload.common.constants.DataUploadUtilityConstants.ACTION_INVALID_MESSAGE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FAILED;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE;
import static com.hbc.dataupload.helper.UploadBufferDataConstants.TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
 class UploadBufferDataServiceTest {

  @InjectMocks UploadBufferService uploadBufferService;
  @InjectMocks private TestUtil testUtil;
  @Mock NodeCarrierFeign nodeCarrierFeign;
  @Mock TransitFeign transitFeign;

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(uploadBufferService, "basePath", "");
    ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 10240);
    ReflectionTestUtils.setField(uploadBufferService, "maxRows", 1000);
  }

  @Test
  void UploadBufferDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        "Node ServiceOption Buffer Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
  void UploadBufferInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void UploadBufferInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void UploadBufferLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 1);

    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void UploadBufferEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void UploadBufferLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(uploadBufferService, "maxRows", 30);

    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_largeRowSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void UploadBufferDataFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals(
        NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FAILED, response.getBody().getMessage());
  }

  @Test
  void UploadBufferDataIncorrectDataTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_invalidData.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    //        CommonServiceException exception =
    //                assertThrows(CommonServiceException.class,
    //                        () ->
    // uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));
    //        Assertions.assertEquals(NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INCORRECT_DATA,
    // exception.getMessage());
    BaseResponse<NodeCarrierResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
  }

  @Test
  void UploadBufferDataPartialSuccessTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "nodeServiceBufferData",
            "nodeServiceOptionBuffer_dateNull.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    BaseResponse<NodeCarrierResponse> baseResponse =
        testUtil.getBaseResponseOfNodeCarrierResponse();
    when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
  }

  // transit tests
  @Test
   void uploadTransitBufferDataSuccessTest() throws CommonServiceException, IOException {
    Path resourceDir =
        Paths.get("src", "test", "resources", "transitBufferData", "transitBuffer_happyPath.csv");
    String absolutePath = resourceDir.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> baseResponse = testUtil.getBaseResponseOfTransitResponse();
    when(transitFeign.updateTransitBufferDetails(any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadTransitBufferData(absolutePath);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals(
        "Transit Buffer Data successfully uploaded!", response.getBody().getMessage());
  }

  @Test
   void uploadTransitBufferDataPartialSuccessTest()
      throws CommonServiceException, IOException {
    Path resourceDir =
        Paths.get("src", "test", "resources", "transitBufferData", "transitBuffer_dateNull.csv");
    String absolutePath = resourceDir.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> baseResponse = testUtil.getBaseResponseOfTransitResponse();
    when(transitFeign.updateTransitBufferDetails(any())).thenReturn(baseResponse);
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadTransitBufferData(absolutePath);

    assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
  }

  @Test
   void uploadTransitBufferInvalidHeadersExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_invalidHeaders.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));

    Assertions.assertEquals(
        TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
   void uploadTransitBufferInvalidFileTypeExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_invalidFileType.html");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));
    Assertions.assertEquals(TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
   void uploadTransitBufferLargeFileSizeExceptionTest() {
    ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 1);
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_largeFileSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));
    Assertions.assertEquals(TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
   void uploadTransitBufferLargeRowSizeExceptionTest() {
    ReflectionTestUtils.setField(uploadBufferService, "maxRows", 30);
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_largeRowSize.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));
    Assertions.assertEquals(TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
   void uploadTransitBufferEmptyRecordsExceptionTest() {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_emptyRecords.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    CommonServiceException exception =
        assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));
    Assertions.assertEquals(TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
    Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
   void uploadTransitBufferFailureTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transitBufferData", "transitBuffer_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadTransitBufferData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("Transit Buffer Data upload failed!", response.getBody().getMessage());
  }

  @Test
   void uploadTransitBufferInvalidActionTest() throws CommonServiceException, IOException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "transitBuffer_invalidAction.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));
    assertEquals(ACTION_INVALID_MESSAGE, exception.getMessage());
  }
}
