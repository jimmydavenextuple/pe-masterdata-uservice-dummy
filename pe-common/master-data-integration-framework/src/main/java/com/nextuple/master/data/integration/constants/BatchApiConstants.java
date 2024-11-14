/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.master.data.integration.constants;

public class BatchApiConstants {
  public BatchApiConstants() {} // NOSONAR

  public static final String TOTAL_RECORDS = "Total Number of Records processed";
  public static final String TOTAL_RECORDS_EXAMPLE = "1000";
  public static final String SUCCESSFUL_RECORDS = "Total Number of Successful Records";
  public static final String SUCCESSFUL_RECORDS_EXAMPLE = "500";
  public static final String FAILED_RECORDS = "Total Number of Failed Records";
  public static final String FAILED_RECORDS_EXAMPLE = "500";
  public static final String RECORD_NUMBER = "Record Number";
  public static final String RECORD_NUMBER_EXAMPLE = "1";
  public static final String STATUS_CODE = "Status Code of the response";
  public static final String STATUS_CODE_EXAMPLE = "200";
  public static final String MESSAGE = "Response Message";
  public static final String MESSAGE_EXAMPLE = "200 OK: Records fetched successfully";
  public static final String EXCEPTION_MESSAGE = "Exception Message for the Failed records";
  public static final String EXCEPTION_MESSAGE_EXAMPLE = "500: Internal Server error";
  public static final String RESPONSES = "List of all the responses obtained";
  public static final String PAYLOAD = "Request Payload";
  public static final String PAYLOAD_EXAMPLE =
      "{\n"
          + "    \"nodeId\": \"66689775853252\",\n"
          + "    \"nodeLabourTier\" : \"tier-1\",\n"
          + "    \"street\": \"9797Rombauer Rd\", \n"
          + "    \"nodeType\": \"Store\",\n"
          + "    \"isActive\": true,\n"
          + "    \"city\": \"Dallas\", \n"
          + "    \"country\": \"US\",  \n"
          + "    \"latitude\": \"0\",   \n"
          + "    \"serviceOptionEligibilities\": {\n"
          + "        \"expressEligible\": false,\n"
          + "        \"sdndEligible\": false,\n"
          + "        \"nextdayEligible\": false\n"
          + "    },\n"
          + "    \"shipToHome\": true,\n"
          + "    \"bopisEligible\": false,\n"
          + "    \"longitude\": \"0\",  \n"
          + "    \"timezone\": \"UTC\" ,\n"
          + "    \"zipCode\": \"43253\",\n"
          + "    \"state\" : \"US\" \n"
          + "    }";
}
