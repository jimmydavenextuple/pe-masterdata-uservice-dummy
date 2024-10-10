/*
 * Copyright (c) 2024., Nextuple, Inc. and/or its affiliates. All rights reserved.
 *
 * The software, code and related documentation made available to you by Nextuple, Inc. are provided under a written agreement containing restrictions on use and disclosure and are protected by copyright and other intellectual property laws. As described in and unless expressly permitted in your agreement, you may not use, copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, or display any part, in any form, or by any means. Reverse engineering, disassembly, or de-compilation of this software, unless required by law or permitted via contract for interoperability, is strictly prohibited.
 * The information contained herein is subject to change without notice and is not warranted to be error-free. If you find any errors, please report them to us in writing.
 */

package com.nextuple.calendar.consumer.dto;

import static com.nextuple.calendar.consumer.constants.CalendarConstants.*;

import com.nextuple.master.data.integration.dto.CommonMasterDataFieldsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PickupCalendarFeedDto extends CommonMasterDataFieldsDto implements Serializable {

  private static final long serialVersionUID = 7610346085068919692L;

  @Schema(description = CALENDAR_ID, maxLength = 40, example = CALENDAR_ID_EXAMPLE)
  private String calendarId;

  @Schema(description = NODE_ID, maxLength = 40, example = NODE_ID_EXAMPLE)
  private String nodeId;

  @Schema(description = CARRIER_SERVICE_ID, maxLength = 40, example = CARRIER_SERVICE_ID_EXAMPLE)
  private String carrierServiceId;

  @Schema(description = EFFECTIVE_DATE, example = EFFECTIVE_DATE_EXAMPLE)
  private String effectiveDate;

  @Schema(description = NODE_CS_DESCRIPTION, example = NODE_CS_DESCRIPTION_EXAMPLE)
  private String description;
}
