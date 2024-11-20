/*
 * Copyright (c) 2022., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.csvdownload.common;

import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CALENDAR_ID;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.CUSTOM_REGION_NAME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.EFFECTIVE_DATE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LAST_PICKUP_TIME;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LATITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.LONGITUDE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.NODE_TYPE;
import static com.nextuple.dataupload.common.constants.DataUploadUtilityConstants.SERVICE_NAME;
import static org.junit.jupiter.api.parallel.Resources.TIME_ZONE;

import com.nextuple.calendar.domain.outbound.CarrierServiceCalendarResponse;
import com.nextuple.calendar.domain.outbound.NodeCalendarResponse;
import com.nextuple.carrier.domain.outbound.CarrierServiceResponse;
import com.nextuple.common.base.PagePayload;
import com.nextuple.common.base.PagePayload.Pagination;
import com.nextuple.common.response.BaseResponse;
import com.nextuple.common.response.PreSignedUrlResponse;
import com.nextuple.csvdownload.domain.pojo.DownloadErrorTransitData;
import com.nextuple.csvdownload.domain.pojo.ProcessingLeadTimesRaw;
import com.nextuple.dataupload.common.outbound.NodeAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.NodeCarrierServiceAndServiceOptionResponse;
import com.nextuple.dataupload.common.outbound.ProcessingTimeBufferResponse;
import com.nextuple.dataupload.common.pojo.ActiveCombination;
import com.nextuple.dataupload.common.pojo.ProcessingTimeBuffer;
import com.nextuple.jobs.framework.common.domain.enums.JobStatusEnum;
import com.nextuple.jobs.framework.common.domain.enums.JobTypeEnum;
import com.nextuple.jobs.framework.common.domain.outbound.FileResponse;
import com.nextuple.jobs.framework.common.domain.outbound.JobResponse;
import com.nextuple.jobs.framework.common.domain.pojo.AuditLog;
import com.nextuple.jobs.framework.common.domain.pojo.JobDto;
import com.nextuple.jobs.framework.common.domain.pojo.RecordStatusDto;
import com.nextuple.node.carrier.domain.outbound.NodeCarrierResponse;
import com.nextuple.node.domain.dto.NodeDto;
import com.nextuple.plt.domain.pojo.JobRecordDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionDto;
import com.nextuple.postal.code.timezone.api.domain.dto.CustomRegionInfo;
import com.nextuple.postal.code.timezone.api.domain.dto.PostalCodeTimezoneDto;
import com.nextuple.postal.code.timezone.api.domain.outbound.CustomRegionResponse;
import com.nextuple.postal.code.timezone.api.domain.outbound.PostalCodeResponse;
import com.nextuple.promise.domain.CoreEngineResponse;
import com.nextuple.promise.domain.OrderLinesSourcing;
import com.nextuple.promise.domain.OrderSolution;
import com.nextuple.promise.domain.ShipDestinationDetails;
import com.nextuple.promise.domain.SourceNodeDetails;
import com.nextuple.promise.domain.SourcingError;
import com.nextuple.promise.domain.SourcingItem;
import com.nextuple.promise.domain.SourcingLineException;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.HolidayCutoffDetailsResponse;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PageResponseForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.outbound.PaginationAttributeForHolidayCutoff;
import com.nextuple.promise.sourcing.rule.api.domain.pojo.HolidayCutoffColumnInfoDto;
import com.nextuple.sourcing.cost.config.dto.CostFactorHeadersInfoDto;
import com.nextuple.sourcing.cost.config.dto.FilterCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.RateCardColumnsDto;
import com.nextuple.sourcing.cost.config.dto.RateCardRowsDto;
import com.nextuple.sourcing.cost.config.dto.SelectorCostFactorInfoDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.CostFactorDescriptionDto;
import com.nextuple.sourcing.cost.config.dto.costtyypesdto.SelectorCfInfo;
import com.nextuple.sourcing.cost.config.inbound.CostDefinitionRequest;
import com.nextuple.sourcing.cost.config.outbound.CostDefinitionResponse;
import com.nextuple.sourcing.cost.config.outbound.CostTypeValidationResponse;
import com.nextuple.sourcing.cost.config.outbound.CostValueResponse;
import com.nextuple.transit.domain.dto.TransitTimeEntriesDto;
import com.nextuple.transit.domain.enums.TransitBufferConfigRequestStatusEnum;
import com.nextuple.transit.domain.inbound.TransitBufferConfigRequest;
import com.nextuple.transit.domain.outbound.TransferScheduleResponse;
import com.nextuple.transit.domain.outbound.TransitBufferConfigResponse;
import com.nextuple.transit.domain.outbound.TransitResponse;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TestUtil {

  public static final String ORG_ID = "BAY";
  public static final String CARRIER_SERVICE_ID = "ALL_SDND";
  public static final String CARRIER_ID = "01";

  public static final String SOURCE_REGION = "ON";
  public static final String DESTINATION_REGION = "DEL";
  public static final String SOURCE_FSA = "A0A";
  public static final String DESTINATION_FSA = "M1R";
  public static final String NODE_ID = "nodeId";
  public static final String JOB_ID = "jobId1";
  public static final String SERVICE_OPTION = "serviceOptions";
  public static final String COUNTRY = "CA";
  public static final String STREET = "1963 Boul. Lionel-Bertrand";
  public static final String CITY = "Boisbriand";
  public static final String STATE = "QC";
  public static final String ZIP_CODE = "J7H 1N8";
  public static final String ZIP_CODE_PREFIX = "J7H";
  public static final boolean isActive = true;
  public static final Double PROCESSING_TIME = 20.0;
  public static final String NODE_ID_2 = "nodeId2";
  private static final String SERVICE_OPTION_2 = "EXPRESS";
  public static final String templateType = "nodeCarrier";

  public static final String templateTypeInvalid = "invalid";

  public static final String tenantServiceOption = "SDND,EXPRESS";
  public static final String REGION_ID = "ID1011";
  public static final String REGION_ID_2 = "ID1012";
  public static final String REGION_NAME = "regionName";
  public static final List<String> CODES = Arrays.asList("T2P", "T3P");

  public static final String invalidTemplateTypeErrMsg = "Invalid template type";

  public static final String FILE_PATH =
      "promise-s3-lambda-dev/ui/node-carrier/2022-10-21/market-region.csv";
  public static final long FILE_METADATA_ID = 1L;
  public static final String BUCKET_NAME = "promise-s3-lambda-dev";
  public static final String FILE_NAME = "postalCodeTimezone.csv";
  public static final String DEFAULT = "DEFAULT";
  public static final String CONTENT_TYPE = "text/csv";
  public static final Long CONTENT_LENGTH = 100L;
  public static final String SHIPPING_COST = "SHIPPING_COST";
  public static final String NODE_PROCESSING_COST = "NODE_PROCESSING_COST";
  public static final String NODE_IDS = "node-01,node-02";
  public static final String NODE_TYPE = "MFC";
  private static final String CUSTOM_REGION_DESC = "Some Description";

  public static final Map<String, String> getTenantCustomAttribute() {
    Map<String, String> customAttributes = new HashMap<>();
    customAttributes.put("key1", "value1");
    customAttributes.put("key2", "value2");
    return customAttributes;
  }

  public static final Map<String, String> getTenantLinesCustomAttribute() {
    Map<String, String> lineCustomAttributes = new HashMap<>();
    lineCustomAttributes.put("key3", "value3");
    lineCustomAttributes.put("key4", "value4");
    return lineCustomAttributes;
  }

  public static final String nodeCsvData =
      "action,nodeId,orgId,street,city,state,zipCode,country,latitude,longitude,timezone,shipToHome,bopisEligible,nodeType,isActive\n"
          + "CREATE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE\n"
          + "UPDATE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE\n"
          + "DELETE,1957,NEXTUPLE,Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE";

  public static final String nodeCsvExpectedData =
      "action,nodeId,orgId,street,city,state,zipCode,country,latitude,longitude,timezone,shipToHome,bopisEligible,nodeType,isActive,sdndEligible,expressEligible\n"
          + "CREATE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE,FALSE,FALSE\n"
          + "UPDATE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE,FALSE,FALSE\n"
          + "DELETE,1957,"
          + ORG_ID
          + ",Calgary Downtown,Calgary,AB,T2P 1B5,CA,52.1977,-113.8721,America/Edmonton,TRUE,FALSE,MFC,TRUE,FALSE,FALSE";

  public static final String transitCsvData =
      "orgId,NEXTUPLE,,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3,SGZ4,SGZ5,SGZ6,SGZ7,SGZ8,SGZ9,SGZ10\n"
          + "DGZ1,10,9.96,9.96,DELETE,DELETE,7,7,8.09,DELETE,DELETE\n"
          + "DGZ2,DELETE,DELETE,DELETE,DELETE,9,7.81,7.81,7.89,7.89,D\n"
          + "DGZ3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
          + "DGZ4,10,DELETE,DELETE,DELETE,9.96,8.09,8.09,7.89,8.09,8.09\n"
          + "DGZ5,10,9.96,9.96,9.96,9.96,7.81,7.81,DELETE,DELETE,DELETE\n"
          + "DGZ6,DELETE,9.96,DELETE,DELETE,DELETE,7,7,8.09,8.09,8.09\n"
          + "DGZ7,DELETE,DELETE,DELETE,10,10,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
          + "DGZ9,10,9.5,9.5,9.5,9.5,DELETE,DELETE,DELETE,DELETE,DELETE\n"
          + "DGZ10,DELETE,DELETE,DELETE,8,8,8.09,8.09,DELETE,DELETE,DELETE";

  public static final String transitCsvExpectedData =
      "orgId,"
          + ORG_ID
          + ",,,,,,,,,\n"
          + "Carrier Service:,ALL-Standard,,,,,,,,,\n"
          + "Destination geoZone / Source geoZone ->,SGZ1,SGZ2,SGZ3,SGZ4,SGZ5,SGZ6,SGZ7,SGZ8,SGZ9,SGZ10\n"
          + "DGZ1,10,9.96,9.96,DELETE,DELETE,7,7,8.09,DELETE,DELETE\n"
          + "DGZ2,DELETE,DELETE,DELETE,DELETE,9,7.81,7.81,7.89,7.89,D\n"
          + "DGZ3,10,9,9,9,9,9.5,9.5,7,7.89,7.89\n"
          + "DGZ4,10,DELETE,DELETE,DELETE,9.96,8.09,8.09,7.89,8.09,8.09\n"
          + "DGZ5,10,9.96,9.96,9.96,9.96,7.81,7.81,DELETE,DELETE,DELETE\n"
          + "DGZ6,DELETE,9.96,DELETE,DELETE,DELETE,7,7,8.09,8.09,8.09\n"
          + "DGZ7,DELETE,DELETE,DELETE,10,10,7.81,7.81,7.89,7.89,7.89\n"
          + "DGZ8,10,9.96,9.96,9.96,9.96,7.81,7.81,8.09,6,6\n"
          + "DGZ9,10,9.5,9.5,9.5,9.5,DELETE,DELETE,DELETE,DELETE,DELETE\n"
          + "DGZ10,DELETE,DELETE,DELETE,8,8,8.09,8.09,DELETE,DELETE,DELETE";

  public static final String carrierServiceCsvData =
      "action,orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions\n"
          + "DELETE,NEXTUPLE,A_SDND,ALL,ALL-SDND,All SDND Carrier Services,SDND\n"
          + "CREATE,NEXTUPLE,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "UPDATE,NEXTUPLE,GoFor,GoFor,GoFor-SDND,GoFor,SDND";

  public static final String carrierServiceCsvExpectedData =
      "action,orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions\n"
          + "DELETE,"
          + ORG_ID
          + ",A_SDND,ALL,ALL-SDND,All SDND Carrier Services,SDND\n"
          + "CREATE,"
          + ORG_ID
          + ",GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "UPDATE,"
          + ORG_ID
          + ",GoFor,GoFor,GoFor-SDND,GoFor,SDND";

  public static final String processingLeadTimesCsvData =
      "action,nodeId,orgId,serviceOptions,processingTime (in hrs)\n"
          + "UPDATE,1554,BAY,SDND,2\n"
          + "UPDATE,1560,BAY,SDND,2\n"
          + "UPDATE,1101,BAY,SDND,2\n"
          + "DELETE,1518,BAY,NEXTDAY,6\n"
          + "UPDATE,1634,BAY,EXPRESS,30.92\n"
          + "UPDATE,1601,BAY,EXPRESS,22.55\n"
          + "DELETE,1125,BAY,EXPRESS,19.90\n"
          + "UPDATE,1114,BAY,SDND,24.97";

  public static final String nodeCarrierCsvData =
      "orgId,carrierId,carrierName,carrierServiceId,serviceName,serviceOptions"
          + "\n"
          + "BAY,GoFor,GoFor,GoFor-SDND,GoFor,SDND\n"
          + "BAY,TFORCE,TForce,TFORCE-NEXTDAY,TForce NextDay Guaranteed,NEXTDAY\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-STANDARD,Canada Post Expedited Parcel,STANDARD\n"
          + "BAY,CanadaPost,Canada Post,CanadaPost-EXPRESS,Canada Post Xpresspost,EXPRESS\n"
          + "BAY,UPSN,UPS,UPSN-STANDARD,UPS Standard,STANDARD";

  public static final String expectedEddComputationData =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone\n"
          + "BAY,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC\n"
          + "BAY,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,2,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC";

  public static final String eddComputationDataWithCustomAttributes =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone,lines_customAttributes_key3,lines_customAttributes_key4,customAttributes_key1,customAttributes_key2\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,value1,value2,value3,value4\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,2,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,value1,value2,value3,value4\n";
  public static final String eddComputationData =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,2,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC\n";

  public static final String eddComputationData1 =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,STANDARD,4321,793845835,CHECKOUT,ABC 123,ABC,SK,US,UTC,15,2,STANDARD,item1,ELECTRONICS,in,ABC 123,ABC,SK,US,UTC,\n";

  public static final String eddComputationData2 =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n";

  public static final String eddComputationData3 =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,STANDARD,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,2,STANDARD,item1,ELECTRONICS,in,ABC 123,ABC,SK,US,UTC,\n";
  public static final String eddComputationDataWith4Orders =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1286,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1287,156465898,CHECKOUT,V0B 1B5,VOB,SK,US,America/Toronto,10,1,EXPRESS,item1,ELECTRONICS,in,V0B 1B5,V0B,SK,US,America/Toronto,\n"
          + "NEXTUPLE,STANDARD,,156465899,CART,V0A 1B5,VOA,SK,US,America/Toronto,5,1,STANDARD,item2,ELECTRONICS,in,V0A 1B5,V0A,SK,US,America/Toronto,\n"
          + "NEXTUPLE,SDND,,156465100,CHECKOUT,V0B 1B5,VOB,SK,US,UTC,7,1,SDND,item2,ELECTRONICS,in,V0B 1B5,V0B,SK,US,UTC,\n";

  public static final String eddComputationDataWith6Orders =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,156465897,1286,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,156465897,1286,CHECKOUT,V0B 1B5,VOB,SK,US,America/Toronto,10,1,EXPRESS,item1,ELECTRONICS,in,V0B 1B5,V0B,SK,US,America/Toronto,\n"
          + "NEXTUPLE,STANDARD,,1289,CART,V0A 1B5,VOA,SK,US,America/Toronto,5,1,STANDARD,item2,ELECTRONICS,in,V0A 1B5,V0A,SK,US,America/Toronto,\n"
          + "NEXTUPLE,SDND,156465100,1290,CHECKOUT,V0B 1B5,VOB,SK,US,UTC,7,1,SDND,item2,ELECTRONICS,in,V0B 1B5,V0B,SK,US,UTC,\n"
          + "NEXTUPLE,SDND,156465101,1291,CART,CHECKOUT,V0B 1B5,VOB,SK,US,UTC,7,1,SDND,item2,ELECTRONICS,in,V0B 1B5,V0B,SK,US,UTC,\n"
          + "NEXTUPLE,NEXTDAY,156465102,1292,CHECKOUT,V0A 1B5,VOA,SK,US,UTC,8,1,NEXTDAY,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n";

  public static final String eddComputationDataWith16Orders =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,128d6,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,128gs6,15646fds5897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12asdf86,1564dfsa65897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12ef86,15646daf5897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12dcs86,15646ga5897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,128sdf6,156465e897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,128ag6,1564ade65897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,128hfj6,1564asd65897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12hka86,156465afew897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12ma86,156465re897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12jiw86,15646jt5897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,12ael86,15646er5897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1lkj286,15646589tr7,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1kah286,15646584t97,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,128dskjfa6,156465rer897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1288dskbjfa6,156465rhkjer897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n";

  public static final String eddComputationDataWith4OrderLines =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone"
          + "\n"
          + "NEXTUPLE,EXPRESS,1234,156465897,CART,V0A 1B5,VOA,SK,US,UTC,10,1,EXPRESS,item1,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1234,15646fds5897,CART,V0A 1B5,VOA,SK,US,UTC,10,2,EXPRESS,item2,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1234,1564dfsa65897,CART,V0A 1B5,VOA,SK,US,UTC,10,3,EXPRESS,item3,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n"
          + "NEXTUPLE,EXPRESS,1234,15646daf5897,CART,V0A 1B5,VOA,SK,US,UTC,10,4,EXPRESS,item4,ELECTRONICS,in,V0A 1B5,V0A,SK,US,UTC,\n";

  public static final String eddComputationDataWithEmptyCsvData =
      "orgId,serviceOptions,cartId,sessionId,pageName,shipToAddress_zipCode,shipToAddress_region,shipToAddress_state,shipToAddress_country,shipToAddress_timezone,lines_requiredQty,lines_lineId,lines_serviceOption,lines_item_itemId,lines_item_productClass,lines_item_unitOfMeasure,lines_shipToAddress_zipCode,lines_shipToAddress_region,lines_shipToAddress_state,lines_shipToAddress_country,lines_shipToAddress_timezone";
  public static final Optional<String> STATUS = Optional.empty();

  public static final JobTypeEnum jobType = JobTypeEnum.getTypeFromString("any");
  public static final String processingLeadTimesRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\",\"carrierServiceId\":\"ALL-SDND\",\"serviceOption\":\"SDND\",\"processingTime\":2.0,\"lastPickupTime\":\"00:00\"}";
  public static final String transitTimesRequestBodyJson =
      "{\"orgId\":\"BAY\",\"sourceGeozone\":\"A0A\",\"destinationGeozone\":\"M1R\",\"carrierServiceId\":\"ALL-SDND\",\"transitDays\":\"1.5\"}";

  public static final String nodeRequestBodyJson =
      "{\"nodeId\":\"1554\",\"orgId\":\"BAY\""
          + "    \"street\": \"100 Metropolitan Rd.\",\n"
          + "    \"city\": \"Scarborough\",\n"
          + "    \"state\": \"ON\",\n"
          + "    \"zipCode\": \"M1R 5A2\",\n"
          + "    \"country\": \"Canada\",\n"
          + "    \"latitude\": \"43.769912\",\n"
          + "    \"longitude\": \"-79.296678\",\n"
          + "    \"timezone\": \"America/Toronto\",\n"
          + "    \"shipToHome\": true,\n"
          + "    \"bopisEligible\": true,\n"
          + "    \"serviceOptionEligibilities\": {\n"
          + "        \"expressEligible\": true,\n"
          + "        \"sdndEligible\": true,\n"
          + "        \"nextdayEligible\": true\n"
          + "    },\n"
          + "    \"nodeType\": \"MFC\",\n"
          + "    \"isActive\": true\n"
          + "}";

  public static final String nodeCarrierRequestBodyJson =
      "{\n"
          + "    \"nodeId\": \"NXTHBC01\",\n"
          + "    \"orgId\": \"NXT\",\n"
          + "    \"carrierServiceId\": \"A_STD\",\n"
          + "    \"serviceOption\": \"Standard\",\n"
          + "    \"processingTime\": 20.2,\n"
          + "    \"lastPickupTime\": \"19:00\"\n"
          + "}";
  public static final String nodeCalendarRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"nodeId\": \"DC-963-565\",\n"
          + "    \"effectiveDate\": \"2022-07-24\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static final String calendarRequestBodyJson =
      "{\"action\":\"CREATE\","
          + "\"calendarId\":\"DSV_2022\","
          + "\"orgId\":\"BAY\","
          + "\"description\":\"Monthly\","
          + "\"isMondayWorking\":true,"
          + "\"isTuesdayWorking\":false,"
          + "\"isWednesdayWorking\":true,"
          + "\"isThursdayWorking\":false,"
          + "\"isFridayWorking\":true,"
          + "\"isSaturdayWorking\":false,"
          + "\"isSundayWorking\":true,"
          + "\"exceptionDays\":\"[{\\\"date\\\":\\\"2023-01-01\\\",\\\"reason\\\":\\\"NewYear'sDay\\\"}]\""
          + "}";

  public static final String calendarEmptyExceptionDaysRequestBodyJson =
      "{\"action\":\"CREATE\","
          + "\"orgId\":\"BAY\","
          + "\"calendarId\":\"DSV_2022\","
          + "\"description\":\"DSVs Operations Calendar\","
          + "\"isFridayWorking\":true,"
          + "\"isMondayWorking\":true,"
          + "\"isTuesdayWorking\":true,"
          + "\"isSundayWorking\":false,"
          + "\"isWednesdayWorking\":true,"
          + "\"isSaturdayWorking\":false,"
          + "\"isThursdayWorking\":true,"
          + "\"exceptionDays\":\"\"}";

  public static final String carrierServiceRequestBodyJson =
      "{\n"
          + "    \"orgId\":\"BAY\",\n"
          + "    \"carrierId\":\"A_STD\",\n"
          + "    \"carrierServiceId\":\"ALL-STANDARD\",\n"
          + "    \"carrierName\":\"ALL\",\n"
          + "    \"serviceName\":\"service-1-name\",\n"
          + "    \"serviceOptions\":\"Standard\"\n"
          + "}";
  public static final String carrierServiceCalendarRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"carrierServiceId\": \"ALL-SDND\",\n"
          + "    \"shippingStage\": \"ALL\",\n"
          + "    \"effectiveDate\": \"2022-08-04\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static final String postalCodeTimezoneRequestBodyJson =
      "{\n"
          + "    \"calendarId\": \"test_2\",\n"
          + "    \"orgId\": \"BAY\",\n"
          + "    \"carrierServiceId\": \"ALL-SDND\",\n"
          + "    \"shippingStage\": \"ALL\",\n"
          + "    \"effectiveDate\": \"2022-08-04\",\n"
          + "    \"description\": \"Monthly\"\n"
          + "}";

  public static Double BUFFER_DAYS = 3.0;

  public static final String STORAGE_TYPE = "S3";
  public static final String FILE_PATH_WITH_BUCKET_NAME =
      "promise-s3-lambda-dev/ui/transit-buffer/2022-10-18/fsa_upload..csv";

  public static final String CREATED_BY = "createdBy";

  public JobDto getJobDto() {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES);
    jobDto.setProcessedRecords(0);
    jobDto.setRemainingRecords(2);
    jobDto.setFailureCount(0);
    jobDto.setSuccessCount(0);
    jobDto.setStatus(JobStatusEnum.SUBMITTED);
    jobDto.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    jobDto.setAuditLog(Collections.singletonList(auditLog));
    return jobDto;
  }

  public JobDto getJobDto2() {
    JobDto jobDto = new JobDto();
    jobDto.setJobId(JOB_ID);
    jobDto.setTotalRecords(2);
    jobDto.setJobType(jobType);
    jobDto.setProcessedRecords(0);
    jobDto.setRemainingRecords(2);
    jobDto.setFailureCount(0);
    jobDto.setSuccessCount(0);
    jobDto.setStatus(JobStatusEnum.SUBMITTED);
    jobDto.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    jobDto.setAuditLog(Collections.singletonList(auditLog));
    return jobDto;
  }

  public List<RecordStatusDto> getJobRecordsForProcessingLeadTimes() {
    RecordStatusDto recordDto =
        RecordStatusDto.builder()
            .jobId(JOB_ID)
            .jobType(JobTypeEnum.UPLOAD_PROCESSING_LEAD_TIMES)
            .orgId(ORG_ID)
            .errorMessage("Invalid nodeId")
            .requestBody(processingLeadTimesRequestBodyJson)
            .build();

    return List.of(recordDto);
  }

  public RecordStatusDto getJobRecordsForTransitTimes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_TRANSIT_TIMES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(transitTimesRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForNodes() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODES)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForNodeCarrier() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CARRIER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeCarrierRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForNodeCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_NODE_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(nodeCalendarRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(calendarRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCalendarWithEmptyExceptionDays() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(calendarEmptyExceptionDaysRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCarrierService() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(carrierServiceRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForCarrierServiceCalendar() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_CARRIER_SERVICE_CALENDER)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(carrierServiceCalendarRequestBodyJson)
        .build();
  }

  public RecordStatusDto getJobRecordsForPostalCodeTimezone() {

    return RecordStatusDto.builder()
        .jobId(JOB_ID)
        .jobType(JobTypeEnum.UPLOAD_POSTAL_CODE_TIMEZONE)
        .orgId(ORG_ID)
        .errorMessage("Invalid nodeId")
        .requestBody(postalCodeTimezoneRequestBodyJson)
        .build();
  }

  public ProcessingLeadTimesRaw getProcessingLeadTimesRaw() {
    ProcessingLeadTimesRaw processingLeadTimesRaw = new ProcessingLeadTimesRaw();
    processingLeadTimesRaw.setNodeId(NODE_ID);
    processingLeadTimesRaw.setOrgId(ORG_ID);
    processingLeadTimesRaw.setServiceOption(SERVICE_OPTION);
    processingLeadTimesRaw.setProcessingTime(String.valueOf(PROCESSING_TIME));

    return processingLeadTimesRaw;
  }

  public JobResponse createJobResponse(JobTypeEnum jobTypeEnum, int totalRecords) {
    JobResponse job = new JobResponse();
    job.setJobId(JOB_ID);
    job.setTotalRecords(totalRecords);
    job.setJobType(String.valueOf(jobTypeEnum));
    job.setProcessedRecords(0);
    job.setRemainingRecords(totalRecords);
    job.setFailureCount(0);
    job.setSuccessCount(0);
    job.setStatus(JobStatusEnum.SUBMITTED);
    job.setOrgId(ORG_ID);

    AuditLog auditLog = new AuditLog();
    auditLog.setStatus(JobStatusEnum.SUBMITTED);
    auditLog.setTimeStamp(new Date());
    job.setAuditLog(Collections.singletonList(auditLog));
    return job;
  }

  public TransitResponse getTransitResponse(Float transitDays) {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitResponse.builder()
        .orgId(ORG_ID)
        .sourceGeozone(SOURCE_FSA)
        .destinationGeozone(DESTINATION_FSA)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .transitDays(transitDays)
        .bufferDays(3.0)
        .bufferStartDate(bufferStartDate)
        .bufferEndDate(bufferEndDate)
        .build();
  }

  public TransitTimeEntriesDto getTransitTimeEntriesDto() {
    return TransitTimeEntriesDto.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .totalRecords(2)
        .build();
  }

  public DownloadErrorTransitData getAddTransitDataRequest() {
    DownloadErrorTransitData downloadErrorTransitData = new DownloadErrorTransitData();
    downloadErrorTransitData.setOrgId(ORG_ID);
    downloadErrorTransitData.setSourceGeozone("SSFA");
    downloadErrorTransitData.setDestinationGeozone("DSFA");
    downloadErrorTransitData.setCarrierServiceId("ALL-SDND");
    downloadErrorTransitData.setTransitDays("2");
    return downloadErrorTransitData;
  }

  public PostalCodeTimezoneDto getPostalCodeTimezoneDto() {
    return PostalCodeTimezoneDto.builder()
        .orgId(ORG_ID)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .build();
  }

  public PostalCodeResponse getPostalCodeResponse1() {
    return PostalCodeResponse.builder()
        .orgId(ORG_ID)
        .zipCode(ZIP_CODE)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customRegion("CR1")
        .build();
  }

  public PostalCodeResponse getPostalCodeResponse2() {
    return PostalCodeResponse.builder()
        .orgId(ORG_ID)
        .zipCode(ZIP_CODE)
        .zipCodePrefix(ZIP_CODE_PREFIX)
        .country(COUNTRY)
        .state(STATE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .timeZone(TIME_ZONE)
        .customRegion(null)
        .build();
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse() {
    return CarrierServiceCalendarResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .build();
  }

  public CarrierServiceResponse getCarrierServiceResponse() {
    return CarrierServiceResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .carrierId(CARRIER_ID)
        .serviceName(SERVICE_NAME)
        .serviceOptions(SERVICE_OPTION)
        .build();
  }

  public ActiveCombination getActiveCombination() {
    return ActiveCombination.builder()
        .nodeId(NODE_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .isActive(true)
        .build();
  }

  public Map<String, Double> getProcessingLeadTime() {
    return Map.of("STANDARD", 10.0);
  }

  public NodeCarrierServiceAndServiceOptionResponse
      getNodeCarrierServiceAndServiceOptionResponse() {
    NodeCarrierServiceAndServiceOptionResponse response =
        new NodeCarrierServiceAndServiceOptionResponse();
    response.setNodeId(NODE_ID);
    response.setOrgId(ORG_ID);
    response.setStreet(STREET);
    response.setCity(CITY);
    response.setState(STATE);
    response.setZipCode(ZIP_CODE);
    response.setCarrierServices(List.of(CARRIER_SERVICE_ID));
    response.setServiceOptions(List.of(SERVICE_OPTION));
    response.setActiveCombination(List.of(getActiveCombination()));

    return response;
  }

  public NodeAndServiceOptionResponse getNodeAndServiceOptionResponse() {
    NodeAndServiceOptionResponse response = new NodeAndServiceOptionResponse();
    response.setNodeId(NODE_ID);
    response.setOrgId(ORG_ID);
    response.setNodeType(NODE_TYPE);
    response.setStreet(STREET);
    response.setCity(CITY);
    response.setState(STATE);
    response.setIsActive(isActive);
    response.setServiceOptions(List.of(SERVICE_OPTION));
    response.setProcessingTime(getProcessingLeadTime());

    return response;
  }

  public PagePayload<NodeCarrierServiceAndServiceOptionResponse>
      getNodeCarrierServiceAndServiceOptionResponse(Integer pageNo) {
    PagePayload<NodeCarrierServiceAndServiceOptionResponse> nodeCarrierServicePagePayload =
        new PagePayload<>();

    NodeCarrierServiceAndServiceOptionResponse response =
        getNodeCarrierServiceAndServiceOptionResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(1);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(1);
    nodeCarrierServicePagePayload.setPagination(pagination);
    nodeCarrierServicePagePayload.setData(List.of(response));

    return nodeCarrierServicePagePayload;
  }

  public PagePayload<NodeAndServiceOptionResponse> getNodeAndServiceOptionResponse(Integer pageNo) {
    PagePayload<NodeAndServiceOptionResponse> nodeServicePagePayload = new PagePayload<>();

    NodeAndServiceOptionResponse response = getNodeAndServiceOptionResponse();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(1);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("nodeId");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(1);
    nodeServicePagePayload.setPagination(pagination);
    nodeServicePagePayload.setData(List.of(response));

    return nodeServicePagePayload;
  }

  public BaseResponse<PagePayload<ProcessingTimeBufferResponse>>
      getBaseResponseOfProcessingTimeBuffers() {
    return BaseResponse.builder()
        .message("Processing time buffers fetched successfully")
        .payload(getProcessingTimeBufferPagePayload(1))
        .build();
  }

  public PagePayload<ProcessingTimeBufferResponse> getProcessingTimeBufferPagePayload(int pageNo) {
    PagePayload<ProcessingTimeBufferResponse> processingTimeBufferDtoPagePayload =
        new PagePayload<>();

    ProcessingTimeBufferResponse processingTimeBufferResponse1 =
        getProcessingTimeBufferResponse(NODE_ID);
    ProcessingTimeBufferResponse processingTimeBufferResponse2 =
        getProcessingTimeBufferResponse(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    processingTimeBufferDtoPagePayload.setPagination(pagination);
    processingTimeBufferDtoPagePayload.setData(
        Arrays.asList(processingTimeBufferResponse1, processingTimeBufferResponse2));

    return processingTimeBufferDtoPagePayload;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponse(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(List.of(SERVICE_OPTION, SERVICE_OPTION_2))
        .processingTimeBuffers(
            List.of(
                getProcessingTimeBuffer(SERVICE_OPTION), getProcessingTimeBuffer(SERVICE_OPTION_2)))
        .build();
  }

  private ProcessingTimeBuffer getProcessingTimeBuffer(String serviceOption) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(2.5);
    processingTimeBuffer.setBufferStartDate(new Date(1000));
    processingTimeBuffer.setBufferEndDate(new Date(1000));
    processingTimeBuffer.setStatus("Inactive");
    return processingTimeBuffer;
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponseEmptyValues(String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(new ArrayList<>())
        .processingTimeBuffers(new ArrayList<>())
        .build();
  }

  public ProcessingTimeBufferResponse getProcessingTimeBufferResponsePartialEmptyValues(
      String nodeId) {
    return ProcessingTimeBufferResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .serviceOptions(new ArrayList<>())
        .processingTimeBuffers(List.of(getProcessingTimeBufferWithNullValues(SERVICE_OPTION)))
        .build();
  }

  private ProcessingTimeBuffer getProcessingTimeBufferWithNullValues(String serviceOption) {
    ProcessingTimeBuffer processingTimeBuffer = new ProcessingTimeBuffer();
    processingTimeBuffer.setServiceOption(serviceOption);
    processingTimeBuffer.setBufferHours(2.5);
    processingTimeBuffer.setBufferStartDate(null);
    processingTimeBuffer.setBufferEndDate(null);
    processingTimeBuffer.setStatus(null);
    return processingTimeBuffer;
  }

  public CoreEngineResponse getCoreEngineResponse() {
    OrderLinesSourcing orderLinesSourcing =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("in").itemId("Bag").build())
            .lineId("1")
            .orderedQuantity(10.0)
            .endEstimatedDeliveryDate("2023-07-05T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("FC")
                        .fulfilledQuantity(10L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing));
    orderLinesSourcingMap.put(
        "STANDARD", OrderSolution.builder().orderLinesSourcing(Collections.emptyList()).build());
    List<OrderLinesSourcing> alternateOrderLinesSourcingList =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("in").itemId("Bag").build())
                .lineId("1")
                .orderedQuantity(10.0)
                .endEstimatedDeliveryDate("2023-07-06T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(10L)
                            .build()))
                .build());

    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList));
    alternateOrderLinesSourcingMap.put("STANDARD", Collections.emptyList());
    List<SourcingError> sourcingErrors =
        Collections.singletonList(
            SourcingError.builder().code(1101).message("No Item Present").build());

    List<SourcingLineException> sourcingLineExceptions =
        Collections.singletonList(
            SourcingLineException.builder()
                .item(
                    SourcingItem.builder()
                        .unitOfMeasure("in")
                        .itemId("Bag")
                        .productClass("Accessories")
                        .build())
                .lineId("1")
                .errors(sourcingErrors)
                .build());

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("STANDARD", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS,STANDARD")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(true)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  private static OrderSolution getOrderSolution(OrderLinesSourcing orderLinesSourcing) {
    return OrderSolution.builder()
        .orderLinesSourcing(Collections.singletonList(orderLinesSourcing))
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse2() {
    OrderLinesSourcing orderLinesSourcing =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("1")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing));

    List<OrderLinesSourcing> alternateOrderLinesSourcingList =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("1")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());

    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList));

    List<SourcingLineException> sourcingLineExceptions = Collections.emptyList();

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(false)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse3() {
    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put(
        "EXPRESS", OrderSolution.builder().orderLinesSourcing(Collections.emptyList()).build());

    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put("EXPRESS", Collections.emptyList());

    List<SourcingError> sourcingErrors =
        Collections.singletonList(
            SourcingError.builder().code(1101).message("No Item Present").build());

    List<SourcingLineException> sourcingLineExceptions =
        Collections.singletonList(
            SourcingLineException.builder()
                .item(
                    SourcingItem.builder()
                        .unitOfMeasure("in")
                        .itemId("Bag")
                        .productClass("Accessories")
                        .build())
                .lineId("1")
                .errors(sourcingErrors)
                .build());

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(true)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse4() {
    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put(
        "EXPRESS", OrderSolution.builder().orderLinesSourcing(Collections.emptyList()).build());
    orderLinesSourcingMap.put(
        "STANDARD", OrderSolution.builder().orderLinesSourcing(Collections.emptyList()).build());
    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put("EXPRESS", Collections.emptyList());
    alternateOrderLinesSourcingMap.put("STANDARD", Collections.emptyList());
    List<SourcingError> sourcingErrors =
        Collections.singletonList(
            SourcingError.builder().code(1101).message("No Item Present").build());

    List<SourcingLineException> sourcingLineException1 =
        Collections.singletonList(
            SourcingLineException.builder()
                .item(
                    SourcingItem.builder()
                        .unitOfMeasure("in")
                        .itemId("Bag")
                        .productClass("Accessories")
                        .build())
                .lineId("1")
                .errors(sourcingErrors)
                .build());
    List<SourcingLineException> sourcingLineException2 =
        Collections.singletonList(
            SourcingLineException.builder()
                .item(
                    SourcingItem.builder()
                        .unitOfMeasure("in")
                        .itemId("Bag")
                        .productClass("Accessories")
                        .build())
                .lineId("1")
                .errors(sourcingErrors)
                .build());

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineException1);
    exceptionsList.put("STANDARD", sourcingLineException2);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS,STANDARD")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(true)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse5() {
    OrderLinesSourcing orderLinesSourcing1 =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("1")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing1));
    orderLinesSourcingMap.put("SDND", getOrderSolution(orderLinesSourcing1));
    List<OrderLinesSourcing> alternateOrderLinesSourcingList1 =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("1")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());
    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList1));
    alternateOrderLinesSourcingMap.put(
        "SDND", Collections.singletonList(alternateOrderLinesSourcingList1));
    List<SourcingLineException> sourcingLineExceptions = Collections.emptyList();

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS,SDND")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(false)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse6() {
    OrderLinesSourcing orderLinesSourcing1 =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("1")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();
    OrderLinesSourcing orderLinesSourcing2 =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("2")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing1));
    orderLinesSourcingMap.put("SDND", getOrderSolution(orderLinesSourcing1));
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing2));
    orderLinesSourcingMap.put("SDND", getOrderSolution(orderLinesSourcing2));
    List<OrderLinesSourcing> alternateOrderLinesSourcingList1 =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("1")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());
    List<OrderLinesSourcing> alternateOrderLinesSourcingList2 =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("2")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node3")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());
    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList1));
    alternateOrderLinesSourcingMap.put(
        "SDND", Collections.singletonList(alternateOrderLinesSourcingList1));
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList2));
    alternateOrderLinesSourcingMap.put(
        "SDND", Collections.singletonList(alternateOrderLinesSourcingList2));
    List<SourcingLineException> sourcingLineExceptions = Collections.emptyList();

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS,SDND")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(false)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse7() {
    OrderLinesSourcing orderLinesSourcing1 =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("1")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();
    OrderLinesSourcing orderLinesSourcing2 =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("2")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node2")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put(
        "EXPRESS",
        OrderSolution.builder()
            .orderLinesSourcing(
                new ArrayList<>(Arrays.asList(orderLinesSourcing1, orderLinesSourcing2)))
            .build());

    List<OrderLinesSourcing> alternateOrderLinesSourcingList1 =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("1")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());
    List<OrderLinesSourcing> alternateOrderLinesSourcingList2 =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("2")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node3")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());

    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    List<List<OrderLinesSourcing>> alternateOrderLinesSourcingLists = new ArrayList<>();
    alternateOrderLinesSourcingLists.add(alternateOrderLinesSourcingList1);
    alternateOrderLinesSourcingLists.add(alternateOrderLinesSourcingList2);
    alternateOrderLinesSourcingMap.put("EXPRESS", alternateOrderLinesSourcingLists);

    List<SourcingLineException> sourcingLineExceptions = Collections.emptyList();

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS")
        .cartId("1286")
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region("ABC")
                .state("SK")
                .country("US")
                .timezone("UTC")
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(false)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public CoreEngineResponse getCoreEngineResponse8() {
    OrderLinesSourcing orderLinesSourcing =
        OrderLinesSourcing.builder()
            .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
            .lineId("1")
            .orderedQuantity(5.0)
            .endEstimatedDeliveryDate("2023-07-06T12:30:00")
            .sourceNodes(
                Collections.singletonList(
                    SourceNodeDetails.builder()
                        .sourceNodeId("Node1")
                        .sourceNodeType("MFC")
                        .fulfilledQuantity(5L)
                        .build()))
            .build();

    Map<String, OrderSolution> orderLinesSourcingMap = new HashMap<>();
    orderLinesSourcingMap.put("EXPRESS", getOrderSolution(orderLinesSourcing));

    List<OrderLinesSourcing> alternateOrderLinesSourcingList =
        Collections.singletonList(
            OrderLinesSourcing.builder()
                .item(SourcingItem.builder().unitOfMeasure("each").itemId("phones").build())
                .lineId("1")
                .orderedQuantity(5.0)
                .endEstimatedDeliveryDate("2023-07-07T12:30:00")
                .sourceNodes(
                    Collections.singletonList(
                        SourceNodeDetails.builder()
                            .sourceNodeId("Node2")
                            .sourceNodeType("MFC")
                            .fulfilledQuantity(5L)
                            .build()))
                .build());

    Map<String, List<List<OrderLinesSourcing>>> alternateOrderLinesSourcingMap = new HashMap<>();
    alternateOrderLinesSourcingMap.put(
        "EXPRESS", Collections.singletonList(alternateOrderLinesSourcingList));

    List<SourcingLineException> sourcingLineExceptions = Collections.emptyList();

    Map<String, List<SourcingLineException>> exceptionsList = new HashMap<>();
    exceptionsList.put("EXPRESS", sourcingLineExceptions);

    return CoreEngineResponse.builder()
        .orgId("NEXTUPLE")
        .serviceOption("EXPRESS")
        .cartId("1286")
        .pageName(null)
        .cartId(null)
        .shipDestinationDetails(
            ShipDestinationDetails.builder()
                .zipCode("ABC 123")
                .region(null)
                .state(null)
                .country(null)
                .timezone(null)
                .build())
        .orderLinesSourcingMap(orderLinesSourcingMap)
        .hasErrors(false)
        .alternateOrderLinesSourcingMap(alternateOrderLinesSourcingMap)
        .exceptionsList(exceptionsList)
        .build();
  }

  public TransitBufferConfigRequest getTransitBufferConfigRequest() {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigRequest.builder()
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .filePath(FILE_PATH_WITH_BUCKET_NAME)
        .storageType(STORAGE_TYPE)
        .build();
  }

  public TransitBufferConfigResponse getTransitBufferConfigResponse() {
    Date bufferStartDate = new Date(1000);
    Date bufferEndDate = new Date(1000);
    return TransitBufferConfigResponse.builder()
        .id(1L)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .bufferDays(BUFFER_DAYS)
        .startDate(bufferStartDate)
        .endDate(bufferEndDate)
        .status(TransitBufferConfigRequestStatusEnum.CREATED)
        .build();
  }

  public BaseResponse<TransitBufferConfigResponse> getTransitBufferConfigResponseBaseResponse() {
    BaseResponse<TransitBufferConfigResponse> transitBufferConfigResponseBaseResponse =
        new BaseResponse<>();
    transitBufferConfigResponseBaseResponse.setSuccess(Boolean.TRUE);
    transitBufferConfigResponseBaseResponse.setPayload(getTransitBufferConfigResponse());
    return transitBufferConfigResponseBaseResponse;
  }

  public NodeDto getNodeDto(String nodeId) {
    return NodeDto.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .nodeType(NODE_TYPE)
        .city(CITY)
        .latitude(LATITUDE)
        .longitude(LONGITUDE)
        .isActive(true)
        .build();
  }

  public PagePayload<NodeDto> getNodePagePayload() {
    PagePayload<NodeDto> nodeDtoPagePayload = new PagePayload<>();

    NodeDto nodeDto1 = getNodeDto(NODE_ID);
    NodeDto nodeDto2 = getNodeDto(NODE_ID_2);

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(1);
    pagination.setSortBy("DESC");
    pagination.setTotalRecords(2);
    nodeDtoPagePayload.setPagination(pagination);
    nodeDtoPagePayload.setData(Arrays.asList(nodeDto1, nodeDto2));

    return nodeDtoPagePayload;
  }

  public BaseResponse<PagePayload<NodeDto>> getBaseResponseOfNodePage() {
    return BaseResponse.builder()
        .message("Nodes fetched successfully")
        .payload(getNodePagePayload())
        .build();
  }

  public BaseResponse<List<NodeCarrierResponse>> getBaseResponseOfNodeCarrier() {
    return BaseResponse.builder()
        .message("Node Carriers fetched successfully")
        .payload(getNodeCarrierResponseList())
        .build();
  }

  public List<NodeCarrierResponse> getNodeCarrierResponseList() {
    return List.of(getNodeCarrierResponse(NODE_ID), getNodeCarrierResponse(NODE_ID_2));
  }

  private NodeCarrierResponse getNodeCarrierResponse(String nodeId) {
    return NodeCarrierResponse.builder()
        .nodeId(nodeId)
        .orgId(ORG_ID)
        .carrierServiceId(CARRIER_SERVICE_ID)
        .serviceOption(SERVICE_OPTION)
        .lastPickupTime(LAST_PICKUP_TIME)
        .build();
  }

  public BaseResponse<List<NodeCalendarResponse>> getNodeCalendarBaseResponse() {
    return BaseResponse.builder()
        .message("Node Calendars fetched successfully")
        .payload(getNodeCalendarResponseList())
        .build();
  }

  public List<NodeCalendarResponse> getNodeCalendarResponseList() {
    NodeCalendarResponse nodeCalendarResponse =
        NodeCalendarResponse.builder()
            .nodeId(NODE_ID)
            .orgId(ORG_ID)
            .calendarId(CALENDAR_ID)
            .effectiveDate(EFFECTIVE_DATE)
            .build();
    return List.of(nodeCalendarResponse);
  }

  public FileResponse getFileResponse() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationData.getBytes()))
        .build();
  }

  public FileResponse getFileResponse2() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith4Orders.getBytes()))
        .build();
  }

  public FileResponse getFileResponse3() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith6Orders.getBytes()))
        .build();
  }

  public FileResponse getFileResponse4() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationData2.getBytes()))
        .build();
  }

  public FileResponse getFileResponse5() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationData1.getBytes()))
        .build();
  }

  public FileResponse getFileResponse6() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationData3.getBytes()))
        .build();
  }

  public FileResponse getFileResponse7() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWithEmptyCsvData.getBytes()))
        .build();
  }

  public FileResponse getFileResponse8() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith16Orders.getBytes()))
        .build();
  }

  public FileResponse getFileResponse9() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWith4OrderLines.getBytes()))
        .build();
  }

  public FileResponse getFileResponse10() {
    return FileResponse.builder()
        .filePath(FILE_PATH)
        .fileName(FILE_NAME)
        .bucketName(BUCKET_NAME)
        .contentLength(58L)
        .contentType("text/csv")
        .inputStream(new ByteArrayInputStream(eddComputationDataWithCustomAttributes.getBytes()))
        .build();
  }

  public PreSignedUrlResponse getPreSignedUrlResponse() {
    return PreSignedUrlResponse.builder()
        .signedURL("URL")
        .storageType("s3")
        .filePath("path")
        .build();
  }

  public String outPutDataForSingleLineOrder() {
    return "\"orgId\",\"cartId\",\"sessionId\",\"pageName\",\"serviceOptions\",\"endEstimatedDeliveryDate\",\"endEstimatedShipDate\",\"cutOffTime\",\"lines_lineId\",\"lines_itemId\",\"linesOrderedQuantity\",\"lines_sourceNodes_sourceNodeId\",\"lines_sourceNodes_sourceNodeType\",\"lines_sourceNodes_fulfilledQuantity\",\"shipToAddress_zipCode\",\"shipToAddress_region\",\"shipToAddress_state\",\"shipToAddress_country\",\"shipToAddress_timezone\",\"hasExceptions\",\"exception_lines_lineId\n\",\"exception_lines_errorCode\",\"exception_lines_errorMessage\"\n\"NEXTUPLE\",\"1286\",\"\",\"\",\"EXPRESS\",\"2023-07-06T12:30:00\",,,\"1\",\"phones\",\"5.0\",\"Node1\",\"MFC\",\"5\",\"ABC 123\",\"ABC\",\"SK\",\"US\",\"UTC\",\"false\",\"\",\"\",\"\"\n";
  }

  public String outPutDataForSingleLineOrderMultipleServiceOptions() {
    return "\"orgId\",\"cartId\",\"sessionId\",\"pageName\",\"serviceOptions\",\"endEstimatedDeliveryDate\",\"endEstimatedShipDate\",\"cutOffTime\",\"lines_lineId\",\"lines_itemId\",\"linesOrderedQuantity\",\"lines_sourceNodes_sourceNodeId\",\"lines_sourceNodes_sourceNodeType\",\"lines_sourceNodes_fulfilledQuantity\",\"shipToAddress_zipCode\",\"shipToAddress_region\",\"shipToAddress_state\",\"shipToAddress_country\",\"shipToAddress_timezone\",\"hasExceptions\",\"exception_lines_lineId\n\",\"exception_lines_errorCode\",\"exception_lines_errorMessage\"\n\"NEXTUPLE\",\"1286\",\"\",\"\",\"SDND\",\"2023-07-06T12:30:00\",,,\"1\",\"phones\",\"5.0\",\"Node1\",\"MFC\",\"5\",\"ABC 123\",\"ABC\",\"SK\",\"US\",\"UTC\",\"false\",\"\",\"\",\"\"\n\"NEXTUPLE\",\"1286\",\"\",\"\",\"EXPRESS\",\"2023-07-06T12:30:00\",,,\"1\",\"phones\",\"5.0\",\"Node1\",\"MFC\",\"5\",\"ABC 123\",\"ABC\",\"SK\",\"US\",\"UTC\",\"false\",\"\",\"\",\"\"\n";
  }

  public String outPutDataForMultipleLineOrder() {
    return "\"orgId\",\"cartId\",\"sessionId\",\"pageName\",\"serviceOptions\",\"endEstimatedDeliveryDate\",\"endEstimatedShipDate\",\"cutOffTime\",\"lines_lineId\",\"lines_itemId\",\"linesOrderedQuantity\",\"lines_sourceNodes_sourceNodeId\",\"lines_sourceNodes_sourceNodeType\",\"lines_sourceNodes_fulfilledQuantity\",\"shipToAddress_zipCode\",\"shipToAddress_region\",\"shipToAddress_state\",\"shipToAddress_country\",\"shipToAddress_timezone\",\"hasExceptions\",\"exception_lines_lineId\n\",\"exception_lines_errorCode\",\"exception_lines_errorMessage\"\n\"NEXTUPLE\",\"1286\",\"\",\"\",\"EXPRESS\",\"2023-07-06T12:30:00\",,,\"1\",\"phones\",\"5.0\",\"Node1\",\"MFC\",\"5\",\"ABC 123\",\"ABC\",\"SK\",\"US\",\"UTC\",\"false\",\"\",\"\",\"\"\n\"NEXTUPLE\",\"1286\",\"\",\"\",\"EXPRESS\",\"2023-07-06T12:30:00\",,,\"2\",\"phones\",\"5.0\",\"Node2\",\"MFC\",\"5\",\"ABC 123\",\"ABC\",\"SK\",\"US\",\"UTC\",\"false\",\"\",\"\",\"\"\n";
  }

  public String outputForNoOrderRequest() {
    return "\"orgId\",\"cartId\",\"sessionId\",\"pageName\",\"serviceOptions\",\"endEstimatedDeliveryDate\",\"endEstimatedShipDate\",\"cutOffTime\",\"lines_lineId\",\"lines_itemId\",\"linesOrderedQuantity\",\"lines_sourceNodes_sourceNodeId\",\"lines_sourceNodes_sourceNodeType\",\"lines_sourceNodes_fulfilledQuantity\",\"shipToAddress_zipCode\",\"shipToAddress_region\",\"shipToAddress_state\",\"shipToAddress_country\",\"shipToAddress_timezone\",\"hasExceptions\",\"exception_lines_lineId\n\",\"exception_lines_errorCode\",\"exception_lines_errorMessage\"\n";
  }

  public CarrierServiceCalendarResponse getCarrierServiceCalendarResponse2(String effectiveDate) {
    return CarrierServiceCalendarResponse.builder()
        .carrierServiceId(CARRIER_SERVICE_ID)
        .calendarId(CALENDAR_ID)
        .orgId(ORG_ID)
        .effectiveDate(effectiveDate)
        .build();
  }

  public String createtransitTimesRequestBodyJson(String srcGeozone, String destGeozone) {
    return "{\"orgId\":\"BAY\",\"sourceGeozone\":\""
        + srcGeozone
        + "\",\"destinationGeozone\":\""
        + destGeozone
        + "\",\"carrierServiceId\":\"ALL-SDND\",\"transitDays\":\"2.5\"}";
  }

  public CustomRegionDto getCustomRegionDto(String regionId) {
    return CustomRegionDto.builder()
        .orgId(ORG_ID)
        .id(regionId)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(CODES)
        .build();
  }

  public CustomRegionResponse getCustomRegionResponse() {
    return CustomRegionResponse.builder()
        .orgId(ORG_ID)
        .id(REGION_ID)
        .customRegionName(CUSTOM_REGION_NAME)
        .codes(CODES)
        .build();
  }

  public PagePayload<CustomRegionDto> getCustomRegionPaginatedResponse(Integer pageNo) {
    PagePayload<CustomRegionDto> customRegionDtoPagePayload = new PagePayload<>();

    Pagination pagination = new Pagination();
    pagination.setTotalPages(2);
    pagination.setCurrentPage(pageNo);
    pagination.setSortBy("id");
    pagination.setSortOrder("ASC");
    pagination.setTotalRecords(2);
    customRegionDtoPagePayload.setPagination(pagination);
    customRegionDtoPagePayload.setData(
        List.of(getCustomRegionDto(REGION_ID), getCustomRegionDto(REGION_ID_2)));

    return customRegionDtoPagePayload;
  }

  public CostDefinitionRequest getGridRequest() {
    return CostDefinitionRequest.builder()
        .costType(SHIPPING_COST)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf("carrierServiceId")
                .selectorCfValue("UPS-GROUND")
                .build())
        .filters(
            List.of(
                FilterCostFactorInfoDto.builder()
                    .costFactor("surge")
                    .costFactorValue("NON-HOLIDAY")
                    .build()))
        .row("billWeight")
        .column("zone")
        .build();
  }

  public CostDefinitionRequest getGridRequestWithoutSelector() {
    return CostDefinitionRequest.builder()
        .costType(NODE_PROCESSING_COST)
        .filters(
            List.of(
                FilterCostFactorInfoDto.builder()
                    .costFactor("surge")
                    .costFactorValue("NON-HOLIDAY")
                    .build()))
        .row("billWeight")
        .column("zone")
        .build();
  }

  public CostDefinitionResponse getGridResponse() {
    return CostDefinitionResponse.builder()
        .isRateCardActive(true)
        .columns(
            RateCardColumnsDto.builder()
                .title("Shipping zones")
                .headers(
                    List.of(
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("billWeight")
                            .columnName("Bill Weight")
                            .isCostFactor(true)
                            .build(),
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("zone1")
                            .columnName("Zone 1")
                            .isCostFactor(true)
                            .build(),
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("zone2")
                            .columnName("Zone 2")
                            .isCostFactor(true)
                            .build(),
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("zone3")
                            .columnName("Zone 3")
                            .isCostFactor(true)
                            .build()))
                .build())
        .rows(
            RateCardRowsDto.builder()
                .data(
                    List.of(
                        Map.of(
                            "billWeight",
                            "0<<=5",
                            "zone1",
                            "10",
                            "zone2",
                            "20",
                            "zone3",
                            "30",
                            "isDynamicBucket",
                            "true"),
                        Map.of(
                            "billWeight",
                            "5<<=10",
                            "zone1",
                            "50",
                            "zone2",
                            "100",
                            "zone3",
                            "150",
                            "isDynamicBucket",
                            "true"),
                        Map.of(
                            "billWeight",
                            "10<<=30",
                            "zone1",
                            "100",
                            "zone2",
                            "200",
                            "zone3",
                            "300",
                            "isDynamicBucket",
                            "true"),
                        Map.of(
                            "billWeight",
                            "30+",
                            "zone1",
                            "200",
                            "zone2",
                            "300",
                            "zone3",
                            "400",
                            "isDynamicBucket",
                            "true")))
                .build())
        .build();
  }

  public CostDefinitionRequest getTableRequest() {
    return CostDefinitionRequest.builder()
        .costType(SHIPPING_COST)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf("carrierServiceId")
                .selectorCfValue("UPS-GROUND")
                .build())
        .row("billWeight")
        .build();
  }

  public CostDefinitionResponse getTableResponse() {
    return CostDefinitionResponse.builder()
        .isRateCardActive(true)
        .columns(
            RateCardColumnsDto.builder()
                .title("")
                .headers(
                    List.of(
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("billWeight")
                            .columnName("Bill Weight")
                            .isCostFactor(true)
                            .build(),
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("cost")
                            .columnName("Shipping Cost")
                            .isCostFactor(true)
                            .build()))
                .build())
        .rows(
            RateCardRowsDto.builder()
                .data(
                    List.of(
                        Map.of("billWeight", "0<<=5", "cost", "20", "isDynamicBucket", "true"),
                        Map.of("billWeight", "5<<=10", "cost", "30", "isDynamicBucket", "true"),
                        Map.of("billWeight", "10<<=20", "cost", "40", "isDynamicBucket", "true"),
                        Map.of("billWeight", "20+", "cost", "50", "isDynamicBucket", "true")))
                .build())
        .build();
  }

  public CostDefinitionRequest getStaticTableRequest() {
    return CostDefinitionRequest.builder()
        .costType(SHIPPING_COST)
        .selector(
            SelectorCostFactorInfoDto.builder()
                .selectorCf("carrierServiceId")
                .selectorCfValue("UPS-GROUND")
                .build())
        .build();
  }

  public CostDefinitionResponse getStaticTableResponse() {
    return CostDefinitionResponse.builder()
        .isRateCardActive(true)
        .columns(
            RateCardColumnsDto.builder()
                .title("")
                .headers(
                    List.of(
                        CostFactorHeadersInfoDto.builder()
                            .columnMeta("shippingCostXyz")
                            .columnName("Shipping Cost")
                            .isCostFactor(true)
                            .build()))
                .build())
        .rows(RateCardRowsDto.builder().data(List.of(Map.of("shippingCostXyz", "20"))).build())
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponse() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(SHIPPING_COST)
        .displayName(SHIPPING_COST)
        .selectorCf("carrierServiceId")
        .selectorCfDisplayName("Carrier service Id")
        .selectorCfInfo(
            List.of(
                SelectorCfInfo.builder()
                    .selectorCfValue("UPS-GROUND")
                    .costItinerary("UPS-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("billWeight")
                            .values(List.of("0<<=5", "5<<=10", "10<<=20", "30+"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zone")
                            .values(List.of("zone1", "zone2", "zone3"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                                .build()))
                    .build(),
                SelectorCfInfo.builder()
                    .selectorCfValue("FEDEX-GROUND")
                    .costItinerary("FEDEX-ITN")
                    .row(
                        CostFactorDescriptionDto.builder()
                            .costFactor("billWeight")
                            .values(List.of("0<<=10", "10<<=20", "20<<=30", "40+"))
                            .build())
                    .column(
                        CostFactorDescriptionDto.builder()
                            .costFactor("zone")
                            .values(List.of("zoneA", "zoneB", "zoneC"))
                            .build())
                    .costFactors(
                        List.of(
                            CostFactorDescriptionDto.builder()
                                .costFactor("surge")
                                .values(List.of("WEEKDAY", "WEEKEND"))
                                .build()))
                    .build()))
        .build();
  }

  public CostTypeValidationResponse getCostTypeValidationResponseWithoutSelector() {
    return CostTypeValidationResponse.builder()
        .currency("USD")
        .costType(SHIPPING_COST)
        .displayName(SHIPPING_COST)
        .costItinerary("DEFAULT-ITN")
        .row(
            CostFactorDescriptionDto.builder()
                .costFactor("billWeight")
                .values(List.of("0<<=5", "5<<=10", "10<<=20", "30+"))
                .build())
        .column(
            CostFactorDescriptionDto.builder()
                .costFactor("zone")
                .values(List.of("zone1", "zone2", "zone3"))
                .build())
        .costFactors(
            List.of(
                CostFactorDescriptionDto.builder()
                    .costFactor("surge")
                    .values(List.of("NON-HOLIDAY", "HOLIDAY"))
                    .build(),
                CostFactorDescriptionDto.builder()
                    .costFactor("EDD")
                    .values(List.of("BeforeEDD", "AfterEDD"))
                    .build()))
        .build();
  }

  public CostValueResponse getCostValueResponseWithSlabValue() {
    return CostValueResponse.builder()
        .costValue(10.0)
        .costItinerary("costItinerary")
        .costFactorCombinationKey("A|B|C|D")
        .prevSlabValue("B|C|d")
        .build();
  }

  public CostValueResponse getCostValueResponseWithoutSlabValue() {
    return CostValueResponse.builder()
        .costValue(10.0)
        .costItinerary("costItinerary")
        .costFactorCombinationKey("A|B|C|D")
        .build();
  }

  public List<HolidayCutoffColumnInfoDto> getHCOColumnsDto() {
    return List.of(
        HolidayCutoffColumnInfoDto.builder()
            .columnName("serviceOption")
            .columnMeta("serviceOption")
            .isSortable(true)
            .build());
  }

  public List<Map<String, Object>> getHCORowsDto() {
    HashMap<String, Object> holidayCutoffRows = new HashMap<>();
    holidayCutoffRows.put("serviceOption", "SDND");

    return List.of(holidayCutoffRows);
  }

  public HolidayCutoffDetailsResponse getHCOResponseData() {
    return HolidayCutoffDetailsResponse.builder()
        .columns(getHCOColumnsDto())
        .rows(getHCORowsDto())
        .build();
  }

  public PaginationAttributeForHolidayCutoff getPaginationForHCO() {
    return PaginationAttributeForHolidayCutoff.builder()
        .currentPage(1)
        .totalPages(2)
        .totalRecords(2L)
        .sortOrder("ASC")
        .sortBy("ruleName")
        .build();
  }

  public PageResponseForHolidayCutoff getPageResponseForHCO() {
    return PageResponseForHolidayCutoff.builder()
        .data(getHCOResponseData())
        .pagination(getPaginationForHCO())
        .build();
  }

  public BaseResponse<PageResponseForHolidayCutoff> getBaseResponseForHCO() {
    return BaseResponse.builder().message("").payload(getPageResponseForHCO()).build();
  }

  public BaseResponse<PageResponseForHolidayCutoff> getBaseResponseForHCOWithNullAttributeValue() {
    return BaseResponse.builder()
        .message("")
        .payload(
            PageResponseForHolidayCutoff.builder()
                .data(
                    HolidayCutoffDetailsResponse.builder()
                        .columns(getHCOColumnsDto())
                        .rows(List.of(Map.of("serviceOption", "")))
                        .build())
                .pagination(getPaginationForHCO())
                .build())
        .build();
  }

  public BaseResponse<PageResponseForHolidayCutoff> getBaseResponseForHCOWithoutData() {
    var hcoData =
        HolidayCutoffDetailsResponse.builder().columns(getHCOColumnsDto()).rows(List.of()).build();
    return BaseResponse.builder()
        .message("")
        .payload(PageResponseForHolidayCutoff.builder().data(hcoData).pagination(null).build())
        .build();
  }

  public BaseResponse<PageResponseForHolidayCutoff> getBaseResponseForHCOWithoutConfig() {
    return BaseResponse.builder()
        .message("")
        .payload(PageResponseForHolidayCutoff.builder().data(null).pagination(null).build())
        .build();
  }

  public JobRecordDto getJobRecordDtoForFpm() {
    JobRecordDto jobRecordDto = new JobRecordDto();
    jobRecordDto.setJobId(TestUtil.JOB_ID);
    jobRecordDto.setOrgId(TestUtil.ORG_ID);
    jobRecordDto.setRequestBody("request body");
    jobRecordDto.setResponseBody(
        "{\"success\":false,\"requestId\":\"[55, 97, 101, 102, 57, 54, 49, 57, 45, 54, 102, 51, 100, 45, 52, 102, 54, 54, 45, 97, 48, 98, 49, 45, 99, 50, 97, 55, 48, 98, 52, 49, 53, 48, 48, 55, 35, 49, 52, 57, 57, 48, 53]\",\"timestamp\":1721206709748,\"message\":\"Item not found for given details\",\"payload\":{\"type\":\"ERROR\",\"code\":8192,\"fields\":{\"itemId\":{\"rejectedValue\":\"TESTITEMNEIP\"},\"uom\":{\"rejectedValue\":\"each\"},\"orgId\":{\"rejectedValue\":\"SIGNET\"}}}}");
    jobRecordDto.setRecordNo(1);
    jobRecordDto.setRawRequest("raw request");
    jobRecordDto.setErrorMessage("error message");
    jobRecordDto.setFeedType("itembuffer");
    return jobRecordDto;
  }

  public JobRecordDto getJobRecordDtoForFpmWithErrorMessageAsNull() {
    JobRecordDto jobRecordDto = new JobRecordDto();
    jobRecordDto.setJobId(TestUtil.JOB_ID);
    jobRecordDto.setOrgId(TestUtil.ORG_ID);
    jobRecordDto.setRequestBody("request body");
    jobRecordDto.setResponseBody(
        "{\"success\":false,\"requestId\":\"[55, 97, 101, 102, 57, 54, 49, 57, 45, 54, 102, 51, 100, 45, 52, 102, 54, 54, 45, 97, 48, 98, 49, 45, 99, 50, 97, 55, 48, 98, 52, 49, 53, 48, 48, 55, 35, 49, 52, 57, 57, 48, 53]\",\"timestamp\":1721206709748,\"payload\":{\"type\":\"ERROR\",\"code\":8192,\"fields\":{\"itemId\":{\"rejectedValue\":\"TESTITEMNEIP\"},\"uom\":{\"rejectedValue\":\"each\"},\"orgId\":{\"rejectedValue\":\"SIGNET\"}}}}");
    jobRecordDto.setRecordNo(1);
    jobRecordDto.setRawRequest("raw request");
    jobRecordDto.setErrorMessage("error message");
    return jobRecordDto;
  }

  public ResponseEntity<com.nextuple.plt.domain.pojo.JobDto> getJobDtoForNeip() {
    com.nextuple.plt.domain.pojo.JobDto jobDto = new com.nextuple.plt.domain.pojo.JobDto();
    jobDto.setFeedType("itembuffer");
    return ResponseEntity.ok(jobDto);
  }

  public BaseResponse<PagePayload<TransferScheduleResponse>> getTransferScheduleResponse() {
    TransferScheduleResponse transferScheduleResponse =
        TransferScheduleResponse.builder()
            .dropoffNodeId("Node-1")
            .sourceNodeId("Node-2")
            .startTime(new Date())
            .endTime(new Date())
            .build();
    List<TransferScheduleResponse> transferScheduleResponseList = List.of(transferScheduleResponse);
    PagePayload pagePayload = new PagePayload<TransferScheduleResponse>();
    pagePayload.setData(transferScheduleResponseList);

    BaseResponse<PagePayload<TransferScheduleResponse>> transferScheduleFeignResponse =
        BaseResponse.builder().payload(pagePayload).build();

    return transferScheduleFeignResponse;
  }

  public List<CustomRegionInfo> getTwoCustomRegionInfoList() {
    CustomRegionInfo customRegionInfo1 =
        CustomRegionInfo.builder()
            .customRegionId("CRID1")
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(List.of("T2P", "T3P"))
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(2)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();

    CustomRegionInfo customRegionInfo2 =
        CustomRegionInfo.builder()
            .customRegionId("CRID2")
            .customRegionDescription(CUSTOM_REGION_DESC)
            .customRegionName(CUSTOM_REGION_NAME)
            .zipCodes(List.of("S1P"))
            .citiesCount(1)
            .statesCount(1)
            .zipCodePrefixesCount(1)
            .orgId(ORG_ID)
            .uploadDate("2024-10-10")
            .build();
    return List.of(customRegionInfo1, customRegionInfo2);
  }

  public PagePayload<CustomRegionInfo> getCustomRegionInfoPagePayload() {
    Pagination pagination = new Pagination();
    pagination.setCurrentPage(1);
    pagination.setTotalPages(1);
    pagination.setSortBy("customRegionId");
    pagination.setNext("");
    pagination.setPrevious("");
    PagePayload pagePayload = new PagePayload<>(getTwoCustomRegionInfoList(), pagination, null);
    return pagePayload;
  }
}
