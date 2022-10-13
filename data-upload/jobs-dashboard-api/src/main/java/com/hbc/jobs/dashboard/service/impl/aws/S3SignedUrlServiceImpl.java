package com.hbc.jobs.dashboard.service.impl.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.hbc.common.exception.CommonServiceException;
import com.hbc.common.response.error.FieldError;
import com.hbc.jobs.dashboard.enums.ModuleEnum;
import com.hbc.jobs.dashboard.service.PreSignedUrlInterface;
import com.hbc.jobs.framework.common.domain.outbound.PreSignedUrlResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
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
