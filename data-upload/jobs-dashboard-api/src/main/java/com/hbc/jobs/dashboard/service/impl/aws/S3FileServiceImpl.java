package com.hbc.jobs.dashboard.service.impl.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.jobs.dashboard.domain.mapper.FileMapper;
import com.hbc.jobs.dashboard.domain.outbound.FileResponse;
import com.hbc.jobs.dashboard.service.FileService;
import java.io.File;
import java.util.Map;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class S3FileServiceImpl implements FileService {

  @Autowired private AmazonS3 amazonS3;

  private static final Logger logger = LoggerFactory.getLogger(S3FileServiceImpl.class);
  public static final FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

  @Override
  public void uploadFile(String bucketName, String filePath, File file) {
    try {
      amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file));
    } catch (Exception e) {
      logger.error("Error in uploading file ");
      throw e;
    }
  }

  @Override
  public FileResponse getFile(String bucketName, String filePath) throws CommonServiceException {
    try {
      var s3object = amazonS3.getObject(bucketName, filePath);
      if (ObjectUtils.isEmpty(s3object)) {
        logger.error(
            "File not found in S3 for bucketName = {} and filePath = {}", bucketName, filePath);
        throw new CommonServiceException(
            "File not found !",
            HttpStatus.BAD_REQUEST,
            0x1779,
            Map.of(
                "bucketName",
                FieldError.builder().rejectedValue(bucketName).build(),
                "filePath",
                FieldError.builder().rejectedValue(filePath).build()));
      }
      return INSTANCE.mapToFileDownloadResponse(s3object);
    } catch (Exception e) {
      logger.error("Error in downloading file ");
      throw e;
    }
  }
}
