package com.hbc.jobs.framework.common.service.impl.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.jobs.framework.common.clients.FileMetaDataClient;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import com.hbc.jobs.framework.common.enums.ModuleEnum;
import com.hbc.jobs.framework.common.service.PreSignedUrlInterface;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class S3SignedUrlServiceImpl implements PreSignedUrlInterface {

  @Value("${aws.s3.signed-url-expiry-minutes}")
  private Integer signedUrlExpiryMinutes;

  @Value("${dataupload.bucket-name}")
  private String bucketName;

  @Value("${storage.type}")
  private String storageType;

  @Autowired private AmazonS3 amazonS3;

  @Autowired private FileMetaDataClient fileMetaDataClient;

  private static final Logger logger = LoggerFactory.getLogger(S3SignedUrlServiceImpl.class);

  @Override
  public PreSignedUrlResponse getPreSignedURL(String fileName, String moduleName)
      throws CommonServiceException {
    if (!validateModuleName(moduleName)) {
      throw new CommonServiceException(
          "module name is not valid",
          HttpStatus.BAD_REQUEST,
          0x1778,
          Map.of("moduleName", FieldError.builder().rejectedValue(moduleName).build()));
    }
    var bucketPath =
        String.format(
            "%s/%s/%s/%s",
            bucketName,
            ModuleEnum.UI.getModuleValue(),
            moduleName,
            DateTime.now().toString("yyyy-MM-dd"));
    var file = String.format("%s-%s", new Date().getTime(), fileName);
    return PreSignedUrlResponse.builder()
        .signedURL(generatePreSignedUrl(bucketPath, file))
        .filePath(String.format("%s/%s", bucketPath, file))
        .storageType(storageType)
        .build();
  }

  @Override
  public PreSignedUrlResponse downloadFileURLById(long fileMetadataId)
      throws CommonServiceException {
    var fileMetaDataResponse = fileMetaDataClient.findFileMetadataById(fileMetadataId).getPayload();

    if (fileMetaDataResponse == null) {
      logger.error("File meta data not found for fileMetadataId : {}", fileMetadataId);
      throw new CommonServiceException(
          "File meta data not found.", HttpStatus.BAD_REQUEST, 0x1771, null);
    }

    String[] filePathArray = fileMetaDataResponse.getPath().split("/", 2);

    var expiration = DateTime.now().plusMinutes(signedUrlExpiryMinutes).toDate();
    var signedUrl = amazonS3.generatePresignedUrl(filePathArray[0], filePathArray[1], expiration);
    var url = signedUrl != null ? signedUrl.toString() : "";
    return PreSignedUrlResponse.builder()
        .signedURL(url)
        .filePath(fileMetaDataResponse.getPath())
        .storageType(storageType)
        .build();
  }

  /*
   * This method is used to generate pre signed url for particular object in bucket
   */
  public String generatePreSignedUrl(String bucketName, String key) {
    var generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
    generatePresignedUrlRequest.setExpiration(
        DateTime.now().plusMinutes(signedUrlExpiryMinutes).toDate());
    generatePresignedUrlRequest.setContentType("text/csv");
    var signedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    return signedUrl.toString();
  }

  public boolean validateModuleName(String moduleName) {
    return Arrays.stream(ModuleEnum.values()).anyMatch(t -> t.getModuleValue().equals(moduleName));
  }
}
