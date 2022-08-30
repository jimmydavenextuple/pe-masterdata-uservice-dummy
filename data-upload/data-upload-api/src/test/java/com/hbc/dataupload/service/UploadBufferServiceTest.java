package com.hbc.dataupload.service;

import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.BaseResponse;
import com.hbc.dataupload.util.TestUtil;
import com.hbc.node.carrier.domain.feign.NodeCarrierFeign;
import com.hbc.node.carrier.domain.outbound.NodeCarrierResponse;
import com.hbc.transit.domain.feign.TransitFeign;
import com.hbc.transit.domain.outbound.TransitResponse;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.hbc.dataupload.helper.UploadBufferDataConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UploadBufferServiceTest {
    @InjectMocks
    UploadBufferService uploadBufferService;
    @InjectMocks
    TestUtil testUtil;
    @Mock
    NodeCarrierFeign nodeCarrierFeign;
    @Mock
    TransitFeign transitFeign;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(uploadBufferService, "basePath", "");
        ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 10240);
        ReflectionTestUtils.setField(uploadBufferService, "maxRows", 1000);
    }

    @Test
    public void uploadNodeServiceOptBufferDataSuccessTest() throws CommonServiceException, IOException {
        Path resourceDir =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_happyPath.csv");
        String absolutePath = resourceDir.toFile().getAbsolutePath();
        BaseResponse<NodeCarrierResponse> baseResponse = testUtil.getBaseResponseOfNodeCarrierResponse();
        when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(
                "Node Service Option Buffer Data successfully uploaded!", response.getBody().getMessage());
    }

    @Test
    public void uploadNodeServiceOptBufferInvalidHeadersExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_invalidHeaders.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));

        Assertions.assertEquals(NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadNodeServiceOptBufferInvalidFileTypeExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_invalidFileType.html");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));
        Assertions.assertEquals(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadNodeServiceOptBufferLargeFileSizeExceptionTest() {
        ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 1);
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_largeFileSize.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));
        Assertions.assertEquals(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadNodeServiceOptBufferLargeRowSizeExceptionTest() {
        ReflectionTestUtils.setField(uploadBufferService, "maxRows", 30);
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_largeRowSize.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));
        Assertions.assertEquals(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadNodeServiceOptBufferEmptyRecordsExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer_emptyRecords.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath));
        Assertions.assertEquals(
                NODE_SERVICE_OPTION_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadNodeServiceOptBufferFailureTest() throws CommonServiceException, IOException {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Node Service Option Buffer Data upload failed!"
                , response.getBody().getMessage());
    }

    @Test
    public void uploadNodeServiceOptBufferDataPartialUploadTest() throws CommonServiceException, IOException {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "nodeServiceOptionBuffer.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        BaseResponse<NodeCarrierResponse> baseResponse = testUtil.getBaseResponseOfNodeCarrierResponse();
        when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadNodeServiceOptionBufferData(absolutePath);
        assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
        Assertions.assertEquals(
                "Node Service Option Buffer Data partially uploaded with some rows failed!",
                response.getBody().getMessage());
    }

    // Test methods for Transit Buffer Data upload
    @Test
    public void uploadTransitBufferDataSuccessTest() throws CommonServiceException, IOException {
        Path resourceDir = Paths.get("src", "test", "resources", "uploadBuffer",
                "transitBuffer_happyPath.csv");
        String absolutePath = resourceDir.toFile().getAbsolutePath();

        BaseResponse<NodeCarrierResponse> baseResponse = testUtil.getBaseResponseOfNodeCarrierResponse();
        when(nodeCarrierFeign.updateBuffer(any())).thenReturn(baseResponse);
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadTransitBufferData(absolutePath);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(
                "Transit Buffer Data successfully uploaded!", response.getBody().getMessage());
    }

    @Test
    public void uploadTransitBufferInvalidHeadersExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer_invalidHeaders.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadTransitBufferData(absolutePath));

        Assertions.assertEquals(TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_HEADERS, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadTransitBufferInvalidFileTypeExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer_invalidFileType.html");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadTransitBufferData(absolutePath));
        Assertions.assertEquals(
                TRANSIT_BUFFER_DATA_UPLOAD_INVALID_FILE_TYPE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadTransitBufferLargeFileSizeExceptionTest() {
        ReflectionTestUtils.setField(uploadBufferService, "maxSizeInKiloBytes", 1);
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer_largeFileSize.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadTransitBufferData(absolutePath));
        Assertions.assertEquals(
                TRANSIT_BUFFER_DATA_UPLOAD_LARGE_FILE_SIZE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadTransitBufferLargeRowSizeExceptionTest() {
        ReflectionTestUtils.setField(uploadBufferService, "maxRows", 30);
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer_largeRowSize.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadTransitBufferData(absolutePath));
        Assertions.assertEquals(
                TRANSIT_BUFFER_DATA_UPLOAD_LARGE_ROW_SIZE, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadTransitBufferEmptyRecordsExceptionTest() {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer_emptyRecords.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        CommonServiceException exception =
                assertThrows(CommonServiceException.class,
                        () -> uploadBufferService.uploadTransitBufferData(absolutePath));
        Assertions.assertEquals(
                TRANSIT_BUFFER_DATA_UPLOAD_FILE_EMPTY_RECORDS, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void uploadTransitBufferFailureTest() throws CommonServiceException, IOException {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadTransitBufferData(absolutePath);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Transit Buffer Data upload failed!"
                , response.getBody().getMessage());
    }

    @Test
    public void uploadTransitBufferDataPartialUploadTest() throws CommonServiceException, IOException {
        Path resourceDirectory =
                Paths.get("src", "test", "resources", "uploadBuffer",
                        "transitBuffer.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        BaseResponse<TransitResponse> baseResponse = testUtil.getFailedBaseResponseForTransit();
        when(transitFeign.updateTransitBufferDetails(any())).thenReturn(baseResponse);
        ResponseEntity<BaseResponse<String>> response =
                uploadBufferService.uploadTransitBufferData(absolutePath);
        assertEquals(HttpStatus.MULTI_STATUS, response.getStatusCode());
        Assertions.assertEquals(
                "Transit Buffer Data partially uploaded with some rows failed!",
                response.getBody().getMessage());
    }
}
