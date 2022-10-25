package com.hbc.csvdownload.util;

import com.hbc.csvdownload.common.inbound.GenericUploadRequest;
import com.hbc.csvdownload.common.outbound.GenericUploadResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileMetaDataResponse;
import com.hbc.jobs.framework.common.domain.outbound.FileResponse;
import com.hbc.jobs.framework.common.domain.outbound.JobResponse;
import java.io.InputStream;
import org.mockito.Mockito;

public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final String FILE_PATH =
      "promise-s3-lambda-dev/ui/node-carrier/2022-10-21/market-region.csv";
  public static final long FILE_METADATA_ID = 1L;
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_NAME = "market-region.csv";
  public static final String CONTENT_TYPE = "text/csv";
  public static final Long CONTENT_LENGTH = 100L;
  public static final String JOB_ID = "J1";

  public GenericUploadRequest getGenericUploadRequest() {
    return GenericUploadRequest.builder().orgId(ORG_ID).filePath(FILE_PATH).build();
  }

  public GenericUploadResponse getGenericUploadResponse() {
    return GenericUploadResponse.builder().orgId(ORG_ID).fileMetaDataId(FILE_METADATA_ID).build();
  }

  public FileResponse getFileResponse() {
    return FileResponse.builder()
        .bucketName(BUCKET_NAME)
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .contentLength(CONTENT_LENGTH)
        .contentType(CONTENT_TYPE)
        .inputStream(Mockito.mock(InputStream.class))
        .build();
  }

  public FileMetaDataResponse getFileMetaDataResponse() {
    return FileMetaDataResponse.builder().id(FILE_METADATA_ID).build();
  }

  public JobResponse getJobResponse() {
    JobResponse response = new JobResponse();
    response.setJobId(JOB_ID);
    return response;
  }
}
