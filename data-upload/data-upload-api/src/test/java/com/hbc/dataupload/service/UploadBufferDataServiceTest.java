package com.hbc.dataupload.service;

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
import com.hbc.csvdownload.exception.CsvFormatValidationFailedException;
import com.hbc.csvdownload.exception.JobSubmissionException;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.jobs.framework.common.clients.JobsDashboardClient;
import com.hbc.jobs.framework.common.domain.pojo.JobDto;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import com.opencsv.exceptions.CsvException;
import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
import org.springframework.util.ObjectUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UploadBufferDataServiceTest {

  @InjectMocks UploadBufferService uploadBufferService;
  @InjectMocks private TestUtil testUtil;
  @Mock NodeCarrierFeign nodeCarrierFeign;
  @Mock TransitFeign transitFeign;
  @Mock TransitDataService transitDataService;
  @Mock JobsDashboardClient jobsDashboardClient;

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
  void uploadTransitBufferDataSuccessTest()
      throws CommonServiceException, IOException, CsvException {
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
  void uploadTransitBufferDataNullEndDate() {
    Path resourceDir =
        Paths.get("src", "test", "resources", "transitBufferData", "transitBuffer_dateNull.csv");
    String absolutePath = resourceDir.toFile().getAbsolutePath();

    BaseResponse<TransitResponse> baseResponse = testUtil.getBaseResponseOfTransitResponse();
    when(transitFeign.updateTransitBufferDetails(any())).thenReturn(baseResponse);
    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.uploadTransitBufferData(absolutePath));

    Assertions.assertNotNull(exception);
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
  void uploadTransitBufferFailureTest() throws CommonServiceException, IOException, CsvException {
    Path resourceDirectory =
        Paths.get("src", "test", "resources", "transitBufferData", "transitBuffer_happyPath.csv");
    String absolutePath = resourceDirectory.toFile().getAbsolutePath();
    ResponseEntity<BaseResponse<String>> response =
        uploadBufferService.uploadTransitBufferData(absolutePath);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("Transit Buffer Data upload failed!", response.getBody().getMessage());
  }

  @Test
  void deleteTransitBuffer()
      throws CsvFormatValidationFailedException, CommonServiceException, IOException,
          JobSubmissionException, CsvException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "deleteTransitBuffer_happyPath.csv");

    when(transitDataService.getDistinctGeozonesList(any(), any()))
        .thenReturn(testUtil.geozonesResponse());

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenReturn(BaseResponse.builder().payload(new JobDto()).build());

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    String res = uploadBufferService.deleteTransitBuffer(absolutePath);

    Assertions.assertFalse(ObjectUtils.isEmpty(res));
  }

  @Test
  void deleteTransitBufferInvalidHeaders() {
    Path resourceDirectory =
        Paths.get(
            "src",
            "test",
            "resources",
            "transitBufferData",
            "deleteTransitBuffer_invalidHeaders.csv");

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    Exception exception =
        Assertions.assertThrows(
            CommonServiceException.class,
            () -> uploadBufferService.deleteTransitBuffer(absolutePath));

    Assertions.assertNotNull(exception);
  }

  @Test
  void deleteTransitBufferInvalidValues() {
    Path resourceDirectory1 =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "deleteTransitBuffer_EmptyOrgId.csv");

    Path resourceDirectory2 =
        Paths.get(
            "src",
            "test",
            "resources",
            "transitBufferData",
            "deleteTransitBuffer_emptyCarrierServiceId.csv");

    Path resourceDirectory3 =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "deleteTransitBuffer_emptyAction.csv");

    String emptyOrgId = resourceDirectory1.toFile().getAbsolutePath();

    String emptyCarrierServiceId = resourceDirectory2.toFile().getAbsolutePath();

    String emptyAction = resourceDirectory3.toFile().getAbsolutePath();
    Exception emptyOrgIdException =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> uploadBufferService.deleteTransitBuffer(emptyOrgId));

    Exception emptyCarrierServiceIdException =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> uploadBufferService.deleteTransitBuffer(emptyCarrierServiceId));

    Exception emptyActionException =
        Assertions.assertThrows(
            CsvFormatValidationFailedException.class,
            () -> uploadBufferService.deleteTransitBuffer(emptyAction));

    Assertions.assertNotNull(emptyOrgIdException);
    Assertions.assertNotNull(emptyCarrierServiceIdException);
    Assertions.assertNotNull(emptyActionException);
  }

  @Test
  void deleteTransitBufferFeignException()
      throws CommonServiceException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "deleteTransitBuffer_happyPath.csv");

    when(transitDataService.getDistinctGeozonesList(any(), any()))
        .thenReturn(testUtil.geozonesResponse());

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(
            new FeignException.BadRequest(
                "Failed to create job",
                Request.create(HttpMethod.GET, "", new HashMap<>(), null, null, null),
                "Failed to create job".getBytes()));

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> uploadBufferService.deleteTransitBuffer(absolutePath));

    Assertions.assertNotNull(exception);
  }

  @Test
  void deleteTransitBufferException()
      throws CommonServiceException {
    Path resourceDirectory =
        Paths.get(
            "src", "test", "resources", "transitBufferData", "deleteTransitBuffer_happyPath.csv");

    when(transitDataService.getDistinctGeozonesList(any(), any()))
        .thenReturn(testUtil.geozonesResponse());

    when(jobsDashboardClient.processJobOffline(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("Error while creating job"));

    String absolutePath = resourceDirectory.toFile().getAbsolutePath();

    Exception exception =
        Assertions.assertThrows(
            JobSubmissionException.class,
            () -> uploadBufferService.deleteTransitBuffer(absolutePath));

    Assertions.assertNotNull(exception);
  }
}
