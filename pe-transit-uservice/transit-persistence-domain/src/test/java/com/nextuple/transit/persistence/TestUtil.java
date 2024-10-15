/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.transit.persistence;

public class TestUtil {
  public static final String ORG_ID = "org-1";

  public static final Long TRANS_BUFFER_REQ_JOB_REF_ID = 1L;

  public static final String TRANS_BUFFER_REQ_JOB_REF_EXT_REF_ID = "1";

  public static final Long TransitBufferReqId = 2L;
  public static final Long Id = 1L;
  public static final String EXTERNAL_REFERENCE = "1";

  public static String SOURCE_GEOZONE = "source-geozone-1";
  public static String DESTINATION_GEOZONE = "destination-geozone-1";
  public static String CARRIER_SERVICE_ID = "carrier-service-id-1";
  public static String ZIP_CODE_PREFIX = "AAA";
  public static String ZIP_CODE = "AAABB1";

  public static final String CARRIER_NAME = "carrier-name-1";
  public static final String SERVICE_NAME = "service-name-1";
  public static final String SERVICE_OPTIONS = "service-options-1";
  public static final String CARRIER_ID = "carrier-1";
  public static Float TRANSIT_DAYS = 10F;

  public static Double BUFFER_DAYS = 3.0;

  public static final String SERVICE_OPTION = "serviceOption-1";
  public static final Long ID = 1L;
  public static final String JOB_ID = "1";
  public static final Long FILE_META_DATA_ID = 3L;
  public static final String CREATED_BY = "created-by";
  public static final String STORAGE_TYPE = "S3";
  public static final String FILE_PATH_WITH_BUCKET_NAME =
      "promise-s3-lambda-dev/ui/transit-buffer/2022-10-18/fsa_upload..csv";
  public static final String ACTION = "CREATE";
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_PATH = "ui/transit-buffer/2022-10-18/fsa_upload..csv";
  public static final String DOWNLOAD_FILE_PATH =
      "ui/transit-buffer/2022-10-18/downloads/fsa_upload.csv";
  public static final String FILE_NAME = "fsa_upload.csv";
  public static final Long TRANS_BUFFER_CONFIG_REQUEST_ID = 1L;
  public static final String ZONE = "Zone1";
}
